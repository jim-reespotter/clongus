package mash.pies.syncthing.engine.processors.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeCommandGenerator;
import mash.pies.syncthing.engine.processors.change.SQLChangeCommandGenerator;
import mash.pies.syncthing.engine.processors.connection.SQLConnection;

public class SQLQuery extends ConnectionQuery {

    static Logger logger = LogManager.getLogger();

    private String table;
    private String[] primaryKeyFields;
    private SQLConnection connection;

    public String getTable() {return table;}
    public void setTable (String table) {this.table = table;}
    public String[] getPrimaryKeyfields() {return primaryKeyFields;}
    public void setPrimaryKeyFields(String[] primaryKeyFields) {this.primaryKeyFields = primaryKeyFields;}

    public SQLConnection getConnection() {return connection;}
    public void setConnection(SQLConnection connection) {this.connection = connection;}



    private String sql;
    private PreparedStatement stmt;

    public String getPK() {return getPrimaryKeyfields()[0];}

    @Override
    protected Set<Entity> read(Map<String, String> params) throws Exception {
        
        // to do: add params as WHERE caluse filters/predicates
        sql = "SELECT * FROM "+getTable();
        stmt = connection.getSQLConnection().prepareStatement(sql);

        logger.debug("SQL Query - running: {}", sql);

        ResultSet rs = stmt.executeQuery();
        ResultSetMetaData rsm = rs.getMetaData();
        List<String> columns = new ArrayList<String>();
        for (int i = 0; i < rsm.getColumnCount(); i++)
            columns.add(rsm.getColumnName(i+1));

        Set<Entity> results = new HashSet<Entity> ();

        while (rs.next()) {
            Entity e = new Entity();
            for (String column : columns)
                e.put(column, rs.getObject(column));
            results.add(e);
//            logger.trace("SQL QUery - read '{}' to entity", getIdentifier(e));
        }

        rs.close();
        logger.debug("SQL Query - loaded {} entities", results.size());
        return results;
    }

    @Override
    protected ChangeCommandGenerator<?> getChangeCommandGenerator(Map<String, String> params) {
        return new SQLChangeCommandGenerator(this, params);
    }
}
