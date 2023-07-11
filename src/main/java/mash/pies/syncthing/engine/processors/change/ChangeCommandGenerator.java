package mash.pies.syncthing.engine.processors.change;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.LogBase;
import mash.pies.syncthing.engine.processors.change.valueGenerator.AttributeValueGenerator;
import mash.pies.syncthing.engine.processors.change.valueGenerator.AttributeValueGenerator.ChangedValue;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;
import mash.pies.syncthing.engine.processors.query.Query;
import mash.pies.syncthing.engine.processors.util.Condition;


/**
 * Base class for datasource specific change command generators
 * 
 */
public abstract class ChangeCommandGenerator<Q extends Query> extends LogBase {

    private Q query;
    private Collection<AttributeValueGenerator> avgs;
    private Map<String, String> params;

    public ChangeCommandGenerator(Q query, Map<String, String> params) {
        this.query = query;
        this.params = params;
    }

    Q getQuery() {
        return this.query;
    }

    /**
     * What do you reckon these do? I'm not sure...
     * @return
     */
    protected Map<String, String> getParams() {
        return this.params;
    }

    // to do: think of a better name
    public interface Runner { 
        ChangeCommand exec(Entity e) throws Exception;
    }


    /* These methods work out what change to run, and if the change has actually done anything */
    public Runner getCreateChange() {

        return new Runner () {
            public ChangeCommand exec (Entity e) throws SQLException {
                trace("Generating create change for "+ e.toString());

                return buildCreateChange(generateChange(e, Condition.CREATE));
            }
        };
    }

    public Runner getUpdateChange() {

        return new Runner () {
            public ChangeCommand exec (Entity me) throws SQLException {
                trace("Generating update change for "+ me.toString());

                Map<String, Object> changes = generateChange(me, Condition.UPDATE);
                if (changes.size() == 0)
                    return null;
                else
                    // dont think this needs casting - look to see if its matchedness is used anywhere
                    return buildUpdateChange((MatchedEntity)me, changes);
            }
        };
    }

    public Runner getRemoveChange() {

        return new Runner () {
            public ChangeCommand exec (Entity e) throws SQLException {
                trace("Generating remove change for "+ e.toString());

                Map<String, Object> changes = generateChange(e, Condition.REMOVE);

                // A removal can actually be a 'move out the way' - if any attributes are set we
                // are moving the entity out of the way - no attributes means delete it.
                if (changes.size() > 0)
                    return buildUpdateChange(new MatchedEntity(e, e), changes); // Oooh ugly
                else
                    return buildRemoveChange(e);
            }
        };
    }

    /* these methods write changes (specific to target, eg LDAP, SQL) */
    public abstract ChangeCommand buildCreateChange(Map<String, Object> changes) throws SQLException;

    public abstract ChangeCommand buildUpdateChange(MatchedEntity me, Map<String, Object> changes)
            throws SQLException;

    public abstract ChangeCommand buildRemoveChange(Entity e) throws SQLException;

    public void setAttributeDefinitions(List<AttributeValueGenerator> attributeDefinitions) {
        this.avgs = attributeDefinitions;
    }

    /**
     * this method produces a list of the values that ned changing/setting 
     * @param e
     * @param condition
     * @return
     */
    protected HashMap<String, Object> generateChange(Entity e, Condition condition) {

        HashMap<String, Object> results = new HashMap<String, Object>();

        for (AttributeValueGenerator avg : avgs)
            if (avg.getConditions().contains(condition)) {
                ChangedValue change = avg.getChangedValue(e);
                if (change != null) //how do we set values to null? issue #13
                    results.put(avg.getAttribute(), change.value);
            }

        return results;
    }
}
