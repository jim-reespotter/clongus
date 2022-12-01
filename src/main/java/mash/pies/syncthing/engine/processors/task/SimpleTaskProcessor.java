package mash.pies.syncthing.engine.processors.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeCommand;
import mash.pies.syncthing.engine.processors.change.ChangeCommandGenerator;
import mash.pies.syncthing.engine.processors.change.valueGenerator.AttributeValueGenerator;
import mash.pies.syncthing.engine.processors.filter.Filter;
import mash.pies.syncthing.engine.processors.filter.FilterRule;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;
import mash.pies.syncthing.engine.processors.matcher.Matcher;
import mash.pies.syncthing.engine.processors.matcher.MatcherRule;
import mash.pies.syncthing.engine.processors.query.QuerySpec;

/**
 * Overall process manager... bit of a lashup at the moment
 * 
 */
public class SimpleTaskProcessor extends TaskProcessor {

    private QuerySpec source;
    private QuerySpec target;

    private List<MatcherRule<?>> matchers = new ArrayList<MatcherRule<?>>();
    private List<FilterRule> filters = new ArrayList<FilterRule>();
    private List<AttributeValueGenerator> attributes = new ArrayList<AttributeValueGenerator>();
    
    public QuerySpec getSource() {return source;}
    public void setSource(QuerySpec qc) {this.source = qc;}
    public QuerySpec getTarget() {return target;}
    public void setTarget(QuerySpec qc) {this.target = qc;}
    
    public List<MatcherRule<?>> getMatchers() {return matchers;}
    public void setMatchers(List<MatcherRule<?>> matchers) {this.matchers = matchers;}
    public List<FilterRule> getFilters() {return filters;}
    public void setFilters(List<FilterRule> filters) {this.filters = filters;}
    public List<AttributeValueGenerator> getAttributes() {return attributes;}
    public void setAttributes(List<AttributeValueGenerator> attributes) {this.attributes = attributes;}

    public Collection<ChangeCommand> generateChanges() throws Exception {
        return generateChanges(new HashMap<String, String>());
    }

    /**
     * Generates a set of ChangeCommands to run to pull the system into line.
     * Stages:
     * - pull current config
     * - match existing entities with each other
     * - (filter those you do/don't want changed)
     * - work out what changes to make
     */
    public Collection<ChangeCommand> generateChanges(Map<String, String> params) throws Exception {

        Collection<Entity> sourceData = source.execute(params);
        debug("Read "+sourceData.size()+" source entities from "+source.getQuery().getName());
        Collection<Entity> targetData = target.execute(params);
        debug("Read "+targetData.size()+" target entities from "+target.getQuery().getName());

        Matcher matcher = new Matcher(matchers);
        Set<MatchedEntity> matches = matcher.match(sourceData, targetData);

        debug("Finished matching - "+matches.size()+" entities matched, leaving "+sourceData.size()+" source and "+targetData.size()+" target entities unmatched");

        // TO DO: add a filter here?
        Filter filter = new Filter(filters);

        // Make changes:
        
        Set<ChangeCommand> changes = new HashSet<ChangeCommand>();
        List<AttributeValueGenerator> avgs = getAttributes();
        debug ("Attributes to check: "+ avgs.stream().map(AttributeValueGenerator::getAttribute).collect(Collectors.toList()));
        ChangeCommandGenerator<?> ccg = target.getQuery().getChangeCommandGenerator(avgs, params);

        // creates:
        for (Entity e : filter.filterCreateEntites(sourceData)) {
            ChangeCommand c = ccg.getCreateChange(e);
            debug("Create: "+c.toString());
            changes.add(c);
        }
        // updates:
        for (MatchedEntity e : filter.filterUpdateEntities(matches)) {
            ChangeCommand c = ccg.getUpdateChange((MatchedEntity) e);
            if (c != null) {
                debug("update: "+c.toString());
                changes.add(c);
            }
        }
        // removes:
        for (Entity e : filter.filterRemoveEntities(targetData)) {
            ChangeCommand c = ccg.getRemoveChange(e);
            debug("Remove: "+c.toString());
            changes.add(c);
        }
        // changes.remove(null);

    debug("generated "+ changes.size()+" changes");
        return changes;
    }
}