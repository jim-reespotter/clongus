package mash.pies.syncthing.engine.processors.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeCommandGenerator;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;
import mash.pies.syncthing.engine.processors.matcher.Matcher;
import mash.pies.syncthing.engine.processors.matcher.MatcherRule;

public class MatchQuery extends Query {

    private QuerySpec source;
    private QuerySpec target;

    public QuerySpec getSource() {return source;}
    public void setSource(QuerySpec qc) {this.source = qc;}
    public QuerySpec getTarget() {return target;}
    public void setTarget(QuerySpec qc) {this.target = qc;}

    private List<MatcherRule<?>> matchers = new ArrayList<MatcherRule<?>>();

    public List<MatcherRule<?>> getMatchers() {return matchers;}
    public void setMatchers(List<MatcherRule<?>> matchers) {this.matchers = matchers;}

    // params ignored in this case I think? Or could pass then in to other queries?
    // Use cases?
    @Override
    protected Set<Entity> read(Map<String,String> params) throws Exception {

        Collection <Entity> sourceData = source.execute(params);
        Collection <Entity> targetData = target.execute(params);

        Set <MatchedEntity> matchedEntities = new Matcher(matchers).match(sourceData, targetData);
        
        // why rebag it?
        return new HashSet<Entity>(matchedEntities);
    }

    @Override
    public ChangeCommandGenerator<?> getChangeCommandGenerator(Map <String, String> params) {
        // TODO Auto-generated method stub
        return null;
    }
}
