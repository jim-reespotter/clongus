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
import mash.pies.syncthing.engine.processors.change.ChangeCommandGenerator.Runner;
import mash.pies.syncthing.engine.processors.change.valueGenerator.AttributeValueGenerator;
import mash.pies.syncthing.engine.processors.filter.Filter;
import mash.pies.syncthing.engine.processors.filter.FilterRule;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;
import mash.pies.syncthing.engine.processors.matcher.Matcher;
import mash.pies.syncthing.engine.processors.matcher.MatcherRule;
import mash.pies.syncthing.engine.processors.query.QuerySpec;
import mash.pies.syncthing.engine.processors.util.Condition;

import static mash.pies.syncthing.engine.processors.util.Condition.*;

/**
 * Overall process manager... bit of a lashup at the moment
 * 
 */
public class SimpleTaskProcessor extends TaskProcessor {

    private Thresholds thresholds = new Thresholds();

    public Map<String, String> getThresholds() {return null;}
    public void setThresholds(Map<String, String> thresholds) {this.thresholds = new Thresholds(thresholds);}

    private QuerySpec source;
    private QuerySpec target;

    public QuerySpec getSource() {return source;}
    public void setSource(QuerySpec qc) {this.source = qc;}
    public QuerySpec getTarget() {return target;}
    public void setTarget(QuerySpec qc) {this.target = qc;}

    private List<MatcherRule<?>> matchers = new ArrayList<MatcherRule<?>>();
    private Matcher matcher;

    public List<MatcherRule<?>> getMatchers() {return matchers;}
    public void setMatchers(List<MatcherRule<?>> matchers) {
        this.matchers = matchers;
        matcher = new Matcher(matchers);
    }

    private List<FilterRule> filters = new ArrayList<FilterRule>();
    Filter filter;

    public List<FilterRule> getFilters() {return filters;}
    public void setFilters(List<FilterRule> filters) {
        this.filters = filters;
        filter = new Filter(filters);
    }

    private List<AttributeValueGenerator> avgs = new ArrayList<AttributeValueGenerator>();    

    public List<AttributeValueGenerator> getAttributes() {return avgs;}
    public void setAttributes(List<AttributeValueGenerator> attributes) {this.avgs = attributes;}

    protected Collection<ChangeCommand> process() throws Exception {
        return process(new HashMap<String, String>());
    }

    /* data structure for simplifying the passing of data between processor components */
    private static class EntityData {
        Collection<Entity> source;
        Collection<MatchedEntity> matched;
        Collection<Entity> target;
    }
    /**
     * Generates a set of ChangeCommands to run to pull the system into line.
     * Stages:
     * - pull current config
     * - match existing entities with each other
     * - (filter those you do/don't want changed)
     * - work out what changes to make
     */
    protected Collection<ChangeCommand> process(Map<String, String> params) throws Exception {

        if (params.size() == 0)
            debug("Running task "+getName()+" with no filter parameters");
        else
            debug("Running task "+getName()+" with filter parameters"+params.toString());

        EntityData data = new EntityData();
        data.source = source.execute(params);
        debug("Read "+data.source.size()+" source entities from "+source.getQuery().getName());
        data.target = target.execute(params);
        debug("Read "+data.target.size()+" target entities from "+target.getQuery().getName());

        data.matched = matcher.match(data.source, data.target);
        
        // Make changes:
        Set<ChangeCommand> changes = new HashSet<ChangeCommand>();

        debug ("Attributes to check: "+ avgs.stream().map(AttributeValueGenerator::getAttribute).collect(Collectors.toList()));
        ChangeCommandGenerator<?> ccg = target.getQuery().getChangeCommandGenerator(avgs, params);

        changes.addAll(generateChanges(ccg.getCreateChange(), data, CREATE));
        changes.addAll(generateChanges(ccg.getUpdateChange(), data, UPDATE));
        changes.addAll(generateChanges(ccg.getRemoveChange(), data, REMOVE));
        return changes;
    }    
        
    private Collection<ChangeCommand> generateChanges(Runner runner, EntityData data, Condition condition) throws Exception {

        Collection<? extends Entity> entities;
        switch (condition) {
            case CREATE: entities = data.source; break;
            case UPDATE: entities = data.matched; break;
            case REMOVE: entities = data.target; break;
            default: return null; // and die?
        }

        Collection<Entity> toProcess = filter.filterEntites(entities, condition);
        Set<ChangeCommand> changes = new HashSet<ChangeCommand>();
        for (Entity e : toProcess) {
            ChangeCommand c = runner.exec(e);
            if (c != null) {  
                debug(condition+": "+c.toString());
                changes.add(c);
            }
        }
        if (thresholds.isOver(condition, data, changes)) {
            return new HashSet<ChangeCommand>(); //kicks off if null...
        } else {
            return changes;
        }
    }

    public void closeConnections() {
        source.getQuery().close();
        target.getQuery().close();
    }

    private class Thresholds {

        Map<Condition, Threshold> thresholds = new HashMap<Condition, Threshold> ();

        Thresholds() {
            thresholds.put(CREATE, new Threshold(CREATE, 10, true));
            thresholds.put(UPDATE, new Threshold(UPDATE, 10, true));
            thresholds.put(REMOVE, new Threshold(REMOVE, 10, true));
        }

        Thresholds(Map<String, String> values) {
            this();
            for (String key: values.keySet()) {
                Threshold t = new Threshold(Condition.valueOf(key.toUpperCase()));
                String value = values.get(key);
                if (value.contains("%")) {
                    t.isPercent = true;
                    value = value.substring(0, value.length()-1);
                }
                t.limit = Float.parseFloat(value);
                thresholds.put(t.condition, t);
            }
        } 

        Threshold get(Condition condition) {
            return thresholds.get(condition);
        }

        Map<String, String> asMap() {return null;}

        public boolean isOver(Condition condition, EntityData data, Set<ChangeCommand> changes) {
            Threshold t = thresholds.get(condition);
            switch (condition) {
                case CREATE: return t.isOver(data.matched.size(), changes.size());
                case UPDATE: return t.isOver(data.matched.size(), changes.size());
                case REMOVE: return t.isOver(data.matched.size(), changes.size());
                default: return true;
            }
        }
        private class Threshold {

            Condition condition;
            float limit = -1;
            boolean isPercent = false;

            Threshold(Condition condition) {this.condition = condition;}

            Threshold(Condition condition, float limit, boolean isPercent) {
                this(condition);
                this.limit = limit;
                this.isPercent = isPercent;
            }

            boolean isOver(int currentCount, int changeCount) {
                if (changeCount == 0)
                    return false;
                else  if (limit == -1) {
                    debug("Threshold unset for "+condition+" - running");
                    return false;
                }
                else if (isPercent) 
                    if (changeCount * 100 / currentCount > limit) {
                        info("Not running "+condition+" - threshold exceeded ("+changeCount
                            +" changes out of "+currentCount+" (threshold is "+limit+"%)");
                        return true;
                    }
                    else {
                        trace("Under threshold for "+condition+" - "+changeCount+" changes..."); //to do finish
                        return false;
                    }
                else if (changeCount > limit) {
                    info("Not running "+condition+" - threshold exceeded ("+changeCount
                        +" changes out of "+currentCount+" (threshold is "+limit+")");
                    return true;
                } 
                else {
                    trace("Under threshold for "+condition+" - "+changeCount+" changes..."); //to do finish
                    return false;
                }
            }
        }
    }
}
