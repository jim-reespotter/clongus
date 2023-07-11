package mash.pies.syncthing.engine.processors.filter;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.util.Condition;

public class AllowFilter extends FilterRule {

    public AllowFilter() {
        getConditions().add(Condition.REMOVE);
    }
    @Override
    public boolean allow(Entity e) {
        return true;
    }
}
