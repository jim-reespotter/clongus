package mash.pies.syncthing.engine.processors.filter;

import java.util.Collection;
import java.util.List;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.ProcessorBase;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;

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

    public Collection<Entity> filterCreateEntites(Collection<Entity> sourceEntities) {
        trace("Filter entities to create (to do)");
        return sourceEntities;
    }

    public Collection<Entity> filterRemoveEntities(Collection<Entity> targetEntities) {
        trace("Filter entities to update (to do)");
        return targetEntities;
    }

    public Collection<MatchedEntity> filterUpdateEntities(Collection<MatchedEntity> matchedEntities) {
        trace("Filter entities to remove (to do)");
        return matchedEntities;
    }
}
