package mash.pies.syncthing.engine.processors.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeCommandGenerator;
import mash.pies.syncthing.engine.processors.connection.FileConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the reading and writing to/from CSV files (and variations)
 *
 * Tweaks needed:
 * - exception handling when CSV field count doesn't match QueryConfig
 * - dealing with quotes
 * - CRUD:
 * - READ done
 */
public class CsvQuery extends ConnectionQuery {

  private FileConnection connection;
  
  public FileConnection getConnection() {return connection;}
  public void setConnection(FileConnection connection) {this.connection = connection;}

  private String filename;

  /**
   * Whether to skip the first line (headers are not read, it takes values from 'field' property)
   */
  private boolean headers = false;
  private List <Field> fields = new ArrayList<Field>();
  private String delimiter = ",";
  private String quote = "\"";

  public String getFilename() {return filename;}
  public void setFilename(String filename) {this.filename = filename;}
  public List <Field> getFields() {return fields;}
  public void setFields(List<Field> fields) {this.fields = fields;}
  public boolean getHeaders() {return headers;}
  public void setHeaders(boolean headers) {this.headers = headers;}
  public String getDelimiter() {return delimiter;}
  public void setDelimiter(String delimiter) {this.delimiter = delimiter;}
  public String getQuote() {return quote;}
  public void setQuote(String quote) {this.quote = quote;}

  

  static Logger logger = LogManager.getLogger();


  @Override
  public Set<Entity> read(Map<String, String> params) throws IOException {  // catch this internally?
    Set<Entity> entities = new HashSet<Entity>();

    // TO DO: make input a stream provided by a Connection (streamConnection - incl file and REST connection vs SQLconnection)
    File csvFile = new File(
        getConnection().getPath() + "/" + getFilename());
    logger.debug("CSV QUery - reading file: {}", csvFile.getName());

    BufferedReader br = new BufferedReader(new FileReader(csvFile));

    if (getHeaders())
      br.readLine();

    String line = br.readLine();
    for (; line != null; line = br.readLine()) {
      logger.trace("processing line: " + line);

      try {
        Entity e = processRecord(line);

        // filter which records to return:
        if (params != null && params.size() > 0) {
          for (String key : params.keySet())
            if (e.get(key).toString().equals(params.get(key)))
              entities.add(e);
        } else
          entities.add(e);
      } catch (ArrayIndexOutOfBoundsException e) {
        logger.warn("Failed to read " + line);
      }
    }
    br.close();

    return entities;
  }

  private Entity processRecord(String line) {
    Entity e = new Entity();
    
    List<Field> fields = getFields();

    String[] details = line.split(getDelimiter());

    for (int i = 0; i < fields.size(); i++) {
      // remove quote
      e.put(fields.get(i).getName(), details[i].replaceAll("^\"|\"$", ""));
    }
    return e;
  }

  @Override
  public ChangeCommandGenerator<?> getChangeCommandGenerator(
      Map<String, String> params) {
    // TODO Auto-generated method stub
    return null;
  }

  public static class Field {

    private String name;
    private String type;    //!! might cause REST issues! dataType instead?
    private String format;

    public Field() {}
    public Field(String name, String type, String format) {
      this.name = name;
      this.type = type;
      this.format = format;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getType() {return type;}
    public void setType(String type) {this.type = type;}
    public String getFormat() {return format;}
    public void setFormat(String format) {this.format = format;}
    
  }
}