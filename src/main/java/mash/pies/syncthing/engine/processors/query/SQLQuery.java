package mash.pies.syncthing.engine.processors.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeCommand;
import mash.pies.syncthing.engine.processors.change.ChangeCommandGenerator;
import mash.pies.syncthing.engine.processors.connection.SQLConnection;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;

/**
 * Query to handle more dynamic + vaied SQL reads and writes...
 * 
 * Config:
 * - !SQLQuery
 *   name: SQL query example
 *   connection: *sqlDB
 *   attributes:    ## in order - sub'd in below
 *   - firstname
 *   - lastname
 *   - id
 * 
 *   create:   INSERT INTO userinfo (firstname, lastname) VALUES (?, ?)
 *   retrieve: SELECT firstname, lastname, id FROM userinfo             ## probably wise but not essential that these match attributes
 *   update:   UPDATE userinfo SET firstname = ?, lastname=? WHERE id=?
 *   delete:   DELETE FROM userinfo WHERE id=?
 * 
 */

public class SQLQuery extends ConnectionQuery {

    private SQLConnection connection;

    private List<String> attributes = new ArrayList<String>();
    private String create;
    private String retrieve;
    private String update;
    private String delete;

    public SQLConnection getConnection() {return connection;}
    public void setConnection(SQLConnection connection) {this.connection = connection;}

    public List<String> getAttributes() {return attributes;}
    public void setAttributes(List<String> attributes) {this.attributes = attributes;
    }
    public String getCreate() {return create;}
    public void setCreate(String create) {this.create = create;}

    public String getRetrieve() {return retrieve;}
    public void setRetrieve(String retrieve) {this.retrieve = retrieve;}

    public String getUpdate() {return update;}
    public void setUpdate(String udpate) {this.update = update;}

    public String getDelete() {return delete;}
    public void setDelete(String delete) {this.delete = delete;}

    @Override
    protected Set<Entity> read(Map<String, String> params) throws Exception {

        Set<Entity> results = new HashSet<Entity> ();
        
        PreparedStatement stmt = connection.getSQLConnection().prepareStatement(retrieve);
        ResultSet rs = stmt.executeQuery();
        ResultSetMetaData rsm = rs.getMetaData();

        while (rs.next()) {
            Entity e = new Entity();
            for (int i = 0; i < rsm.getColumnCount(); i++)
                e.put(rsm.getColumnLabel(i+1), rs.getObject(i+1));
            trace("Read row: "+e.toString());
            results.add(e);
        }

        rs.close();
        return results;
    }

    @Override
    protected ChangeCommandGenerator<?> getChangeCommandGenerator(Map<String, String> params) {
        return new SQLChangeCommandGenerator(params);
    }
    
    class SQLChangeCommandGenerator extends ChangeCommandGenerator<SQLQuery> {

        public SQLChangeCommandGenerator(Map<String, String> params) {
            super(SQLQuery.this, params);
        }

        @Override
        public ChangeCommand buildCreateChange(Map<String, Object> changes) throws SQLException {
            List<Object> params = new ArrayList<Object>();
            for (String attribute : attributes)
                params.add(changes.get(attribute));
            return new SQLChange(create, params);
        }

        @Override
        public ChangeCommand buildUpdateChange(MatchedEntity me, Map<String, Object> changes) throws SQLException {
            List<Object> params = new ArrayList<Object>();
            for (String attribute : attributes)
                params.add(changes.get(attribute));
            return new SQLChange(update, params);        }

        @Override
        public ChangeCommand buildRemoveChange(Entity e) throws SQLException {
            List<Object> params = new ArrayList<Object>();
            for (String attribute : attributes)
                params.add(e.get(attribute));
            return new SQLChange(delete, params);
        }

        class SQLChange implements ChangeCommand {

            private String sql;
            private List<Object> params;

            SQLChange(String sql, List<Object> params) {
                this.sql = sql;
                this.params = params;

                // prepare stmtm here
                // count ?s
            }

            @Override
            public void invoke() throws Exception {
                // move up?
                PreparedStatement stmt = connection.getSQLConnection().prepareStatement(sql);
                // Need to only call this as amny times as there are ? in the query.
                for (int i = 0; i < params.size(); i++)
                    switch(params.get(i).getClass().getSimpleName()) {
                        case "String":
                            stmt.setString(i+1, params.get(i).toString());
                            break;
                        case "Long":
                            stmt.setLong(i+1, (Long) params.get(i));
                        default:
                            String thing = params.get(i).getClass().getSimpleName();
                    }
                stmt.execute();
                stmt.close();
            }

            @Override
            public String toString() {
                return sql + params.toString();
            }
        }
    }
}
