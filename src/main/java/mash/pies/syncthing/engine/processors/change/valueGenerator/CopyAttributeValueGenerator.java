package mash.pies.syncthing.engine.processors.change.valueGenerator;

import mash.pies.syncthing.engine.processors.Entity;

/**
 * Generator that takes a value from a field on the source entity, copies it to the target entity
 */
public class CopyAttributeValueGenerator extends AttributeValueGenerator {

    private String source;

    public String getSource() {return source;}
    public void setSource(String source) {this.source = source;}

    @Override
    ChangedValue generateValue(Entity e) {
        return new ChangedValue(e.get(source));
    }
}
