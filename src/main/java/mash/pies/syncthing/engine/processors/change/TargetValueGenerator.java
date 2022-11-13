package mash.pies.syncthing.engine.processors.change;

import java.util.Collection;
import java.util.HashMap;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.valueGenerator.AttributeValueGenerator;
import mash.pies.syncthing.engine.processors.change.valueGenerator.AttributeValueGenerator.Condition;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * generates the target values for attributes for a given entity
 * as generated against the attribute definitions in the config file.
 */
public class TargetValueGenerator {

    static Logger logger = LogManager.getLogger();

    private Collection<AttributeValueGenerator> avgs;

    public TargetValueGenerator(Collection<AttributeValueGenerator> avgs) {
        this.avgs = avgs;
    }

    public HashMap<String, ChangeValue> generateChange(Entity e, Condition condition) {

        HashMap<String, ChangeValue> results = new HashMap<String, ChangeValue>();

        for (AttributeValueGenerator avg : avgs)

            if (avg.getConditions().contains(condition)) {
                ChangeValue change = avg.getChangedValue(e);
                if (change != null) // null == didn't produce a value
                    results.put(avg.getAttribute(), change);
            }

        logger.trace("identified {} potential attribute values", results.size());
        return results;
    }
}
