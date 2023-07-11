package mash.pies.syncthing.engine.processors.change.valueGenerator;

import java.util.Map;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;

/**
 * Generator for bitwise fields
 * 
 * untested
 */
public class BitwiseAttributeValueGenerator extends AttributeValueGenerator {

    private Map<Integer, Boolean> set;
    private int length;

    public Map<Integer, Boolean> getSet() {return set;}
    public void setSet(Map<Integer, Boolean> set) {this.set = set;}

    public int getInitialLength () {return length;}
    public void setInitialLength(int initialLength) {this.length = initialLength;}

    @Override
    ChangedValue generateValue(Entity e) {
        byte [] value;
        if (e instanceof MatchedEntity) {
            value = (byte[]) ((MatchedEntity)e).getMatch().get(this.getAttribute());
        }
        else {
            value = new byte[length];
            for (int i = 0; i < length; i++)
                value[i] = 1;
        }

        for (int bit : set.keySet())
            if (set.get(bit))
                value[bit] = 1;
            else
                value[bit] = 0;
        
        return new ChangedValue(value);
    }
}
