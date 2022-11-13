package mash.pies.syncthing.engine.processors.change.valueGenerator;

import java.util.Collection;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeValue;

/**
 * Sets a apecific value for a target attribute
 */
public class LiteralAttributeValueGenerator extends AttributeValueGenerator {

    private Collection<String> values;

    public Collection<String> getValues() {return values;}
    public void setValues(Collection<String> values) {this.values = values;}

    @Override
    ChangeValue generateValue(Entity e) {

        return new ChangeValue(getUpdateAction(), values);
    }

}
