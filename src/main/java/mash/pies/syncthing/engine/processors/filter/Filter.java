package mash.pies.syncthing.engine.processors.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.ProcessorBase;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;
import mash.pies.syncthing.engine.processors.util.Condition;

/**
 * After matching, this process filters which entities we are going to create
 * changes for
 * 
 */
public class Filter extends ProcessorBase {

    private List<FilterRule> filterRules;

    public Filter(List<FilterRule> filters) {
        this.filterRules = filters;
    }

    /* Generified version of 3 methods below: */
    public Collection<Entity> filterEntites(Collection<? extends Entity> entities, Condition condition) {
        trace("Filter entities to "+condition.toString().toLowerCase());
        Collection<Entity> filtered = new HashSet<Entity> ();

        for (FilterRule rule : filterRules) 
            if (rule.isApplicable(condition))
                for (Entity e : entities) 
                    if (rule.allow(e))
                        filtered.add(e);
        
        return filtered;
    }
/* 
    public Collection<Entity> filterCreateEntites(Collection<? extends Entity> sourceEntities) {
        trace("Filter entities to create");
        Collection<Entity> filtered = new HashSet<Entity> ();

        for (FilterRule rule : filterRules) 
            if (rule.isApplicable(Condition.CREATE))
                for (Entity e : sourceEntities) 
                    if (rule.allow(e))
                        filtered.add(e);
        
        return filtered;
    }

    public Collection<MatchedEntity> filterUpdateEntities(Collection<MatchedEntity> matchedEntities) {
        trace("Filter entities to update");
        Collection<MatchedEntity> filtered = new HashSet<MatchedEntity>();

        for (FilterRule rule : filterRules) 
            if (rule.isApplicable(Condition.UPDATE))
                for (MatchedEntity e : matchedEntities) 
                if (rule.allow(e))
                    filtered.add(e);

        return filtered;
    }

    public Collection<Entity> filterRemoveEntities(Collection<Entity> targetEntities) {
        trace("Filter entities to remove");
        Collection<Entity> filtered = new HashSet<Entity> ();

        for (FilterRule rule : filterRules) 
            if (rule.isApplicable(Condition.REMOVE))
                for (Entity e : targetEntities) 
                if (rule.allow(e))
                    filtered.add(e);

        return filtered;
    }
*/
}
