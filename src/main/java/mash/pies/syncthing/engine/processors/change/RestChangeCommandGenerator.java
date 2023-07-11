package mash.pies.syncthing.engine.processors.change;

import java.sql.SQLException;
import java.util.Map;

import com.google.api.client.json.GenericJson;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;
import mash.pies.syncthing.engine.processors.query.JsonQuery;

public class RestChangeCommandGenerator extends ChangeCommandGenerator<JsonQuery> {

    public RestChangeCommandGenerator(JsonQuery query, Map<String, String> params) {
        super(query, params);
    }

    @Override
    public ChangeCommand buildCreateChange(Map<String, Object> changes) throws SQLException {
        return new RestCreateChangeCommand(changes);
    }
    
    class RestCreateChangeCommand extends RestChangeCommand {

        GenericJson obj = new GenericJson();

        RestCreateChangeCommand(Map<String, Object> changes) {
            for (String key : changes.keySet())
                obj.put(key, changes.get(key).toString());
        }

        @Override
        public void invoke() throws Exception {
            getQuery().create(obj);
        }

        @Override
        public String toString() {
            try {
                return "Create: "+obj.toPrettyString();
            }
            catch (Exception e) {
                return e.getMessage();
            }
        }
    }

    @Override
    public ChangeCommand buildUpdateChange(MatchedEntity me, Map<String, Object> changes) throws SQLException {
        return new RestUpdateChangeCommand(me, changes);
    }
    
    class RestUpdateChangeCommand extends RestChangeCommand {

        GenericJson obj = new GenericJson();
        String id;

        RestUpdateChangeCommand (MatchedEntity me, Map<String, Object> changes) {
            for (String key : changes.keySet())
                obj.put(key, changes.get(key).toString());
            id = me.getMatch().get("id").toString();
        }

        @Override
        public void invoke() throws Exception {
            getQuery().update(id, obj);
        }

        //@Override
        public String toString() {
            return "Update: "+obj.toString();
        }
    }

    @Override
    public ChangeCommand buildRemoveChange(Entity e) throws SQLException {
        return new RestRemoveChangeCommand(e);
    }
        
    class RestRemoveChangeCommand extends RestChangeCommand {

        GenericJson obj = new GenericJson();

        RestRemoveChangeCommand (Entity e) {
            obj.putAll(e);
        }

        @Override
        public void invoke() throws Exception {
            getQuery().remove(obj);
        }

        public String toString() {
            return "Remove " + obj.toString();
        }
   }

    abstract class RestChangeCommand implements ChangeCommand {}
}
