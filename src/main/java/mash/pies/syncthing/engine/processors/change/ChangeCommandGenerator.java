package mash.pies.syncthing.engine.processors.change;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.LogBase;
import mash.pies.syncthing.engine.processors.change.valueGenerator.AttributeValueGenerator;
import mash.pies.syncthing.engine.processors.change.valueGenerator.AttributeValueGenerator.Condition;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;
import mash.pies.syncthing.engine.processors.query.Query;


/**
 * Base class for datasource specific change command generators
 * 
 */
public abstract class ChangeCommandGenerator<Q extends Query> extends LogBase {

    private Q query;
    protected TargetValueGenerator tvg;
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

    public ChangeCommand getCreateChange(Entity e) throws Exception {

        trace("Generating create change for "+ e.toString());

        return buildCreateChange(tvg.generateChange(e, Condition.CREATE));
    }

    public ChangeCommand getUpdateChange(MatchedEntity me) throws Exception {

        trace("Generating update change for "+ me.toString());

        Map<String, ChangeValue> changes = tvg.generateChange(me, Condition.UPDATE);
        if (changes.size() == 0)
            return null;
        else
            return buildUpdateChange(me, changes);
    }

    public ChangeCommand getRemoveChange(Entity e) throws Exception {
        Map<String, ChangeValue> changes = tvg.generateChange(e, Condition.REMOVE);
        // A removal can actually be a 'move out the way' - if any attributes are set we
        // are moving the entity out of the way - no atrtibutes means delete it.
        if (changes.size() > 0)
            return buildUpdateChange(new MatchedEntity(e, e), changes); // Oooh ugly
        else
            return buildRemoveChange(e);
    }

    public abstract ChangeCommand buildCreateChange(Map<String, ChangeValue> changes) throws SQLException;

    public abstract ChangeCommand buildUpdateChange(MatchedEntity me, Map<String, ChangeValue> changes)
            throws SQLException;

    public abstract ChangeCommand buildRemoveChange(Entity e) throws SQLException;

    public void setAttributeDefinitions(List<AttributeValueGenerator> attributeDefinitions) {
        tvg = new TargetValueGenerator(attributeDefinitions);
    }
}
