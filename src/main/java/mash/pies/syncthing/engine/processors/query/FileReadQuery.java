package mash.pies.syncthing.engine.processors.query;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.qos.logback.core.joran.conditional.ElseAction;
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
  
    public String getFilename() {return filename;}
    public void setFilename(String filename) {this.filename = filename;}
    public List <Field> getFields() {return fields;}
    public void setFields(List<Field> fields) {this.fields = fields;}
    public String getPattern() {return pattern.pattern();}
    public void setPattern(String pattern) {this.pattern = Pattern.compile(pattern);}



    @Override
    protected Set<Entity> read(Map<String, String> params) throws Exception {

        getLogger().debug("Reading file entries from "+filename+ " with filter: "+params.toString());
        Set<Entity> results = new HashSet<Entity>();
        BufferedReader br = new BufferedReader(new FileReader(new File(getConnection().getPath()+"/"+getFilename())));

        while (br.ready()) {
            String line = br.readLine();
            Matcher m = pattern.matcher(line);
            if (m.find()) {
                Entity e = new Entity();
                for (Field f : fields)
                    e.put(f.getName(), m.group());
                
                getLogger().trace("imported "+e.toString());
                results.add(e);
            }
            else
                getLogger().warn("Failed to read line "+line+" from file "+filename);
        }

        br.close();
        return results;
    }

    @Override
    protected ChangeCommandGenerator<?> getChangeCommandGenerator(Map<String, String> params) {
        return null;
    }
}
