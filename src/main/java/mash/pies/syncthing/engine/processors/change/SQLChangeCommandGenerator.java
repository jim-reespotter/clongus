package mash.pies.syncthing.engine.processors.change;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;
import mash.pies.syncthing.engine.processors.query.SQLQuery;

public class SQLChangeCommandGenerator extends ChangeCommandGenerator<SQLQuery> {

  public SQLChangeCommandGenerator(SQLQuery query, Map<String, String> params) {
    super(query, params);
  }

  @Override
  public ChangeCommand buildCreateChange(Map<String, ChangeValue> changes)
    throws SQLException {
    String createQuery = "INSERT INTO " + getQuery().getTable() + " (";
    for (String field : changes.keySet()) createQuery += field + ",";

    createQuery = createQuery.substring(0, createQuery.length() - 1); // remove extra ,
    createQuery += ") VALUES (";

    for (String field : changes.keySet()) createQuery +=
      "'" + changes.get(field) + "',";

    createQuery = createQuery.substring(0, createQuery.length() - 1); // remove extra ,

    createQuery += ")";
    return new SQLChange(createQuery);
  }

  @Override
  public ChangeCommand buildUpdateChange(
    MatchedEntity me,
    Map<String, ChangeValue> changes
  )
    throws SQLException {
    if (changes.size() == 0) return null;

    String updateQuery = "UPDATE " + getQuery().getTable() + " SET ";

    for (String c : changes.keySet()) updateQuery +=
      c + "='" + changes.get(c).getValue() + "',";

    updateQuery = updateQuery.substring(0, updateQuery.length() - 1);

    updateQuery +=
      " WHERE " +
      getQuery().getPK() +
      " = " +
      me.get("target." + getQuery().getPK()) +
      "";

    return new SQLChange(updateQuery);
  }

  @Override
  public ChangeCommand buildRemoveChange(Entity e) throws SQLException {
    String removeQuery =
      "DELETE FROM " +
      getQuery().getTable() +
      " WHERE " +
      getQuery().getPK() +
      " = " +
      e.get(getQuery().getPK()) +
      "";
    return new SQLChange(removeQuery);
  }

  class SQLChange implements ChangeCommand {

    private PreparedStatement stmt;
    private String sql;

    SQLChange(String sqlQuery) throws SQLException {
      sql = sqlQuery;
      this.stmt =
        getQuery()
          .getConnection()
          .getSQLConnection()
          .prepareStatement(sqlQuery);
    }

    @Override
    public void invoke() throws Exception {
      stmt.execute();
    }

    @Override
    public String toString() {
      
      return this.sql;
    }
  }
}
