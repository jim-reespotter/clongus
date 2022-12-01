package mash.pies.syncthing.engine.processors.change;

import java.util.Collection;
import java.util.HashMap;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.LogBase;
import mash.pies.syncthing.engine.processors.change.valueGenerator.AttributeValueGenerator;
import mash.pies.syncthing.engine.processors.change.valueGenerator.AttributeValueGenerator.Condition;

/**
 * generates the expected values for attributes for a given target entity
 * as generated against the attribute definitions in the config file.
 */
public class TargetValueGenerator extends LogBase {

    private Collection<AttributeValueGenerator> avgs;

    public TargetValueGenerator(Collection<AttributeValueGenerator> avgs) {
        this.avgs = avgs;
    }

    public HashMap<String, ChangeValue> generateChange(Entity e, Condition condition) {

        HashMap<String, ChangeValue> results = new HashMap<String, ChangeValue>();

        for (AttributeValueGenerator avg : avgs)
            if (avg.getConditions().contains(condition)) {
                ChangeValue change = avg.getChangedValue(e);
                if (change != null) //how do we set values to null? issue #13
                    results.put(avg.getAttribute(), change);
            }

        return results;
    }
}
