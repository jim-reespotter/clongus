package mash.pies.syncthing.engine.processors.change.valueGenerator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.ProcessorBase;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;
import mash.pies.syncthing.engine.processors.util.Condition;
import mash.pies.syncthing.engine.processors.util.UpdateAction;

/**
 * Base class for processors to work out target value for an attribute
 *
 * In addition this (base class) also checks whether the new value represents a
 * change in value
 * (applicable for updates and possibly removes)
 *
 * TO DO: move code from changeGenerator to here.
 * also consider:
 * - multi vs single value attributes
 * - order of multi valued attributes - is it important?
 * - replace/append(/remove?) existing values
 */
public abstract class AttributeValueGenerator extends ProcessorBase {

  private String attribute;
  private UpdateAction updateOption = UpdateAction.OVERWRITE;
  // Only act on some combination of update/create/remove (all by default)
  private Collection<Condition> conditions = Arrays.asList (Condition.CREATE, Condition.UPDATE);

//  private boolean orderMatters = false;
  private boolean caseSensitive = false;

  public String getAttribute() {return attribute;}
  public void setAttribute(String name) {this.attribute = name;}
  public UpdateAction getUpdateAction() {return updateOption;}
  public void setUpdateAction(UpdateAction action) {this.updateOption = action;}
  public Collection<Condition> getConditions() {return conditions;}
  public void setConditions(Collection<Condition> conditions) {this.conditions = conditions;}
  public boolean isCaseSensitive() {return caseSensitive;}
  public void setCaseSensitive(boolean caseSensitive) {this.caseSensitive = caseSensitive;}


  /**
   * Generate a new value for the attribute given an entity.
   * Subclasses are concrete classes for specific generation approaches (Regex,
   * copy, literal etc)
   * 
   * @param e
   * @return
   */
  abstract ChangedValue generateValue(Entity e);

  /**
   * returns a value if:
   * - this is a create, there is no existing vlaue
   * - this is an update, the value needs changing.
   * 
   * For multi values updates, the returned values will be a complete replacement for the existing values (not just the changes)
  **/
  public ChangedValue getChangedValue(Entity e) {
    trace("Generating change "+getName()+ " for "+e.toString()+", attribute "+getAttribute());
    ChangedValue newValue = generateValue(e); 

    // null means this AVG is not applicable, do nothing
    if (newValue == null)
      return null;
    
    // deal with updateaction and multiplicity....
    if (e instanceof MatchedEntity) {
//      trace("change identified as update");
      MatchedEntity me = (MatchedEntity) e;
      Object currentValue = me.getMatch().get(getAttribute());

      if (newValue.value instanceof Collection) {
        Collection<Object> newValues = new HashSet<Object>((Collection<?>) newValue.value);
        Collection<Object> currentValues = new HashSet<Object>((Collection<?>) currentValue);

        switch (getUpdateAction()) {
          case OVERWRITE:
            break;
          case APPEND:
            newValues.addAll(currentValues);
            newValue = new ChangedValue(newValues);
            break;
          case REMOVE:
            currentValues.removeAll(newValues);
            newValue = new ChangedValue(currentValues);
            break;
          case CLEAR:
            newValue = new ChangedValue(new HashSet<Object>());
          default:
            break;
        }
        if (currentValues.equals(newValues))
          return null; // no change...
        else {
          trace("Setting values to "+newValue.toString());
          return newValue;
        }
      }
      else {
        // is an update, single value - do nothing if:
        // - new and current values are null
        // - new and current values are the same
        if ((currentValue == null && newValue.value == null) || (currentValue != null && currentValue.equals(newValue.value))) {
          trace(" - No change required");
          return null;
        }
      }
    }

    // its not an update, return the new value
    return newValue;
  }

  public static class ChangedValue {

    public final Object value;

    ChangedValue(Object value) {this.value = value;}
  }
}
