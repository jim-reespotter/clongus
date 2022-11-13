package mash.pies.syncthing.engine.processors.change;

import mash.pies.syncthing.engine.processors.change.valueGenerator.AttributeValueGenerator.UpdateAction;

/**
 * Represents a change to a specific attribute (of an entity)
 */
public class ChangeValue {
    
    // enum of actions: OVERWRITE, CLEAR, APPEND, REMOVE (last 2 only applicable to multi value attributes)
    private UpdateAction updateAction;
    // the new value to set
    private Object value;

    public ChangeValue(UpdateAction updateAction, Object value) { //T
        this.updateAction = updateAction;
        this.value = value;
    }

    public UpdateAction getAction() {return updateAction;}
    public Object getValue () {return value;}

    public Class<?> getType () {return value.getClass();}
    public String toString() {return value.toString();} // +" ("+action+")";}
}
