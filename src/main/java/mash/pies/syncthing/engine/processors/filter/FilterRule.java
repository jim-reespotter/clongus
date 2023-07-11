package mash.pies.syncthing.engine.processors.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.ProcessorBase;
import mash.pies.syncthing.engine.processors.util.Condition;

public abstract class FilterRule extends ProcessorBase {

    private Set<Condition> conditions = new HashSet<Condition>();
    private List<String> attributes = new ArrayList<String>();

    public FilterRule() {
        conditions.add(Condition.CREATE);
        conditions.add(Condition.UPDATE);
    }

    public Set<Condition> getConditions() {return conditions;}
    public void setConditions(Set<Condition> conditions) {this.conditions = conditions;}

    public List<String> getAttributes() {return attributes;}
    public void setAttributes(List<String> attributes) {this.attributes = attributes;}

    public abstract boolean allow(Entity e);

    public boolean isApplicable(Condition c) {return conditions.contains(c);}
}
