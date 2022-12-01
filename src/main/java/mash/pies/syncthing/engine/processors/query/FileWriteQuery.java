package mash.pies.syncthing.engine.processors.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeCommandGenerator;
import mash.pies.syncthing.engine.processors.change.FileWriteChangeCommandGenerator;
import mash.pies.syncthing.engine.processors.connection.FileConnection;
import mash.pies.syncthing.engine.processors.query.CsvQuery.Field;

/**
 * Write only query that outputs entites to a file an a specified format. 
 * 
 * Example config:
  - query: &SomeFileOut
    !FileWriteQuery  
    name: powershellOut
    description: write powershell expr to do something for each change required
    connection: *HRFiles
    filename: departments.ps
    create: /add-adUser "$1" --samaccountname $2 --validUntil $4 < echo "$3"/
    update: /update-adUser "$1" --samaccountname $2 --validUntil $4/
    remove: /delete-adUser "$1"/
    fields:
    - name: name
    - name: samAccountName
    - name: password
    - name: expiryDate
      type: date
      format: yyyy-MM-ddd

 */
public class FileWriteQuery extends ConnectionQuery {

    private FileConnection connection;
  
    public FileConnection getConnection() {return connection;}
    public void setConnection(FileConnection connection) {this.connection = connection;}
  
    private String filename;
    public boolean append = true;
    private String createPattern;
    private String updatePattern;
    private String removePattern;
    private String delimiter = ":";
    private List <Field> fields = new ArrayList<Field>();
  
    public String getFilename() {return filename;}
    public void setFilename(String filename) {this.filename = filename;}
    public boolean getAppend() {return append;}
    public void setAppend(boolean append) {this.append = append;}
    public List <Field> getFields() {return fields;}
    public void setFields(List<Field> fields) {this.fields = fields;}
    public String getDelimiter() {return this.delimiter;}
    public void setDelimiter(String delimiter) {this.delimiter = delimiter;}
    public String getCreatePattern() {return createPattern;}
    public void setCreatePattern(String pattern) {this.createPattern = pattern;}
    public String getUpdatePattern() {return updatePattern;}
    public void setUpdatePattern(String pattern) {this.updatePattern = pattern;}
    public String getRemovePattern() {return removePattern;}
    public void setRemovePattern(String pattern) {this.removePattern = pattern;}

    /** does nothing */
    @Override
    protected Set<Entity> read(Map<String, String> params) throws Exception {
        return new HashSet<Entity>();
    }

    /**
     * ...
     */
    @Override
    protected ChangeCommandGenerator<?> getChangeCommandGenerator(Map<String, String> params) {
        return new FileWriteChangeCommandGenerator(this, params);
    }    
}
