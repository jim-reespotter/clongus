package mash.pies.syncthing.engine.processors.change.valueGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import mash.pies.syncthing.engine.processors.Entity;

/**
 * Use a groovy script to generate a value. Example:
 * 
 * - !GroovyAttribute
 *   attribute: sAMAccountName
 *   dataType: String                ## returned
 *   sources:
 *   - firstname
 *   - lastname
 *   - birthday
 *   imports:
 *   - java.date.*
 *   script: |
 *     <script here...>
 * 
 * 
 * Sample scripts:
 *   startDate.plusDays(1)     (startDate is a localDate passed in, LocalDate returned)
 *   LocalDateTime.now().getDayOfYear() (int returned)
 *   \"$firstname 'Walrus' $lastname\"   (value sub in strings)
 */
public class GroovyAttributeValueGenerator extends AttributeValueGenerator {

    private List<String> sources = new ArrayList<String> ();
    private String script;
    private String dataType = "String";
    private Set<String> imports = new HashSet<String>();
    private GroovyObject groovy;

    public List<String> getSources() {return sources;}
    public void setSources(List<String> sources) {this.sources = sources; groovy = null;}

    public String getDataType() {return this.dataType;}
    public void setDataType(String dataType) {this.dataType = dataType; groovy = null;}

    public String getScript() {return script;}
    public void setScript(String script) {this.script = script; groovy = null;}

    public Set<String> getImports() {return imports;}
    public void setImports(Set<String> imports) {this.imports = imports; groovy = null;}
        
    private void prepare() throws InstantiationException, IllegalAccessException, IOException {
        String groovyScript = "";
        for (String imp : imports)
            groovyScript += "import "+ imp +";\n";

        groovyScript += "class GroovyAVGFor"+getAttribute()+ " {\n"
                +dataType+" doit(";
            for (int i = 0; i < sources.size(); i++ ){
                groovyScript += sources.get(i);
                if (i < sources.size()-1)
                    groovyScript += ",";
            }    
        groovyScript += ") {\n" + script + "\n } \n}";

        GroovyClassLoader gc = new GroovyClassLoader();
        Class<?> runner = gc.parseClass(groovyScript);
        groovy = (GroovyObject)runner.newInstance();
        gc.close();
    }

    @Override
    ChangedValue generateValue(Entity e) {
        if (groovy == null)
            try{
                prepare();
            }
            catch (Exception ex) {
                throw new RuntimeException (ex);
            }
        List<Object> args = new ArrayList<Object>();
        for (String source : sources)
            args.add(e.get(source));

        return new ChangedValue(groovy.invokeMethod("doit", args.toArray()));
    }    
}
