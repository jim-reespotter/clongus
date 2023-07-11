package mash.pies.syncthing.engine.processors.change.valueGenerator;

import java.util.HashSet;

import mash.pies.syncthing.engine.processors.Entity;

/**
 * Sets a apecific value for a target attribute
 */
public class LiteralAttributeValueGenerator extends AttributeValueGenerator {

    private Object value;

    public Object getValue() {return value;}
    public void setValue(Object value) {this.value = value;}
    public void setValues(HashSet<String> value) {this.value = value;}

    @Override
    ChangedValue generateValue(Entity e) {
        return new ChangedValue(value);
    }
}
