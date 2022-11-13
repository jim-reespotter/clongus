package mash.pies.syncthing.engine.processors.change.valueGenerator;

import java.util.Map;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeValue;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;

/**
 * Generator for bitwise fields
 * 
 * untested
 */
public class BitwiseAttributeValueGenerator extends AttributeValueGenerator {

    private Map<Integer, Boolean> set;

    public Map<Integer, Boolean> getBitsToSet() {return set;}


    @Override
    ChangeValue generateValue(Entity e) {
        int current = 0;
        try {
            if (e instanceof MatchedEntity)
                current = Integer.parseInt(((MatchedEntity) e).getMatch().get(getAttribute()).toString());
        } catch (Exception ex) {
        }

        for (Integer item : set.keySet())
            if (set.get(item))
                current |= (1 << item);
            else
                current &= ~(1 << item);

        return new ChangeValue(getUpdateAction(), current);
    }
}
