package mash.pies.syncthing.engine.processors.connection;

import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection extends Connection {
    
    // config properties:
    private String url;
    private PasswordCredential credential = new PasswordCredential(); // to do: rename to credential
    private String serverCert;
    
    // getters and setters:
    public String getUrl() {return url;}
    public void setUrl(String url) {this.url = url;}
    public PasswordCredential getCredential() {return credential;}
    public void setCredential(PasswordCredential cred) {this.credential = cred;}
    public String getServerCert() {return serverCert;}
    public void setServerCert(String serverCert) {this.serverCert = serverCert;}
    
    public String getType() {return "SQL connection";}

    // the SQL connection:
    private java.sql.Connection connection; // SQL connection!
    
    public java.sql.Connection getSQLConnection() throws SQLException {

        if (connection == null) {
            this.connection = DriverManager.getConnection(
                url,
                credential.getUsername(),
                credential.getPassword()
            );    
        }
        return connection;
    }

    public void close() throws SQLException {
        connection.close();
    }
}
