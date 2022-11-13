package mash.pies.syncthing.engine.processors.query;

import mash.pies.syncthing.engine.processors.connection.LdapConnection;

/**
 * Could possibly share a lot of Search/Object code (esp if added separate Config class?)
 */
public abstract class LdapQuery extends Query  {
    
    private LdapConnection connection;
    
    public LdapConnection getConnection() {return connection;}
    public void setConnection(LdapConnection connection) {this.connection = connection;}
}
