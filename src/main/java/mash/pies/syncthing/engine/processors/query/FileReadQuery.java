package mash.pies.syncthing.engine.processors.query;

import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeCommandGenerator;
import mash.pies.syncthing.engine.processors.connection.FileConnection;
import mash.pies.syncthing.engine.processors.query.CsvQuery.Field;

/**
 * Query to read regex defined chunks files (could be eg XML or Powershell)
 */
public class FileReadQuery extends ConnectionQuery {

    private FileConnection connection;
  
    public FileConnection getConnection() {return connection;}
    public void setConnection(FileConnection connection) {this.connection = connection;}
  
    private String filename;
    private Pattern pattern;
    private List <Field> fields = new ArrayList<Field>();
    private Set <String> ignore = new HashSet<String>();
    private Set<Pattern> ignorePatterns = new HashSet<Pattern>();

    public String getFilename() {return filename;}
    public void setFilename(String filename) {this.filename = filename;}
    public List <Field> getFields() {return fields;}
    public void setFields(List<Field> fields) {this.fields = fields;}
    public String getPattern() {return pattern.pattern();}
    public void setPattern(String pattern) {this.pattern = Pattern.compile(pattern);}

    public Set<String> getIgnore() {return ignore;}
    public void setIgnore(Set<String> ignore) {
        this.ignore = ignore;
        ignorePatterns.clear();
        for (String p : ignore)
            ignorePatterns.add(Pattern.compile(p));
    }

    @Override
    protected Set<Entity> read(Map<String, String> params) throws Exception {

        debug("Reading file entries from "+filename+ " with filter: "+params.toString());
        Set<Entity> results = new HashSet<Entity>();
        // TO DO: nice error - NPE here == no file (or no connection)...
        BufferedReader br = new BufferedReader(new FileReader(new File(getConnection().getPath()+"/"+getFilename()), StandardCharsets.UTF_8));
        while (br.ready()) {
            String line = br.readLine();

            boolean r = true;
            for (Pattern p : ignorePatterns)
                if (p.matcher(line).find()) {
                    r = false;
                    break; 
                }

            if (r) {
                Matcher m = pattern.matcher(line);
                if (m.find()) {
                    Entity e = new Entity();
                    for (int i = 1; i <= m.groupCount(); i++) {
                        String data = m.group(i);
                        Field f = fields.get(i-1);
                        e.put(f.getName(), f.cast(data));
                    }
                    results.add(e);
                    trace("Added: "+e.toString());
                }            
                else
                    warn("Failed to read line "+line+" from file "+filename);
            }
        }

        br.close();
        return results;
    }

    @Override
    protected ChangeCommandGenerator<?> getChangeCommandGenerator(Map<String, String> params) {
        return null;
    }
}
