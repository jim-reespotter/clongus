package mash.pies.syncthing.engine.processors.change.valueGenerator;

import java.util.Arrays;
import java.util.Collection;
import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.ProcessorBase;
import mash.pies.syncthing.engine.processors.change.ChangeValue;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for processors to work out target value for an attribute
 *
 * In addition this (base class) also checks whether the new value represents a
 * change in value
 * (specifically for update here)
 *
 * TO DO: move code from changeGenerator to here.
 * also consider:
 * - multi vs single value attributes
 * - order of multi valued attributes - is it important?
 * - replace/append(/remove?) existing values
 */
public abstract class AttributeValueGenerator extends ProcessorBase {

  public static enum UpdateAction {
    OVERWRITE,  // replace all existing values
    APPEND,     // replace existing value if single valued?
    REMOVE,     // remove a specific value
    CLEAR       // remove all values
  }

  public enum Condition {
    CREATE,
    UPDATE,
    REMOVE
  }

  private String attribute;
  private UpdateAction updateOption = UpdateAction.OVERWRITE;
  // Only act on some combination of update/create/remove (all by default)
  private Collection<Condition> conditions = Arrays.asList (Condition.CREATE, Condition.UPDATE);

  private boolean orderMatters = false;
  private boolean caseSensitive = false;

  public String getAttribute() {return attribute;}
  public void setAttribute(String name) {this.attribute = name;}
  public UpdateAction getUpdateAction() {return updateOption;}
  public Collection<Condition> getConditions() {return conditions;}
  public void setConditions(Collection<Condition> conditions) {this.conditions = conditions;}
  public UpdateAction getUpdateOption() {return updateOption;}
  public void setUpdateOption(UpdateAction action) {this.updateOption = action;}
  public boolean orderMatters() {return orderMatters;}
  public boolean isCaseSensitive() {return caseSensitive;}
  public void setCaseSensitive(boolean caseSensitive) {this.caseSensitive = caseSensitive;}


  static Logger logger = LogManager.getLogger();

  // public boolean runForCondition(Condition condition) {
  // return conditions.contains(condition);
  // }

  /**
   * Generate a new value for the attribute given an entity.
   * Subclasses are concrete classes for specific generation approaches (Regex,
   * copy, literal etc)
   * 
   * @param e
   * @return
   */
  abstract ChangeValue generateValue(Entity e);

  public ChangeValue getChangedValue(Entity e) {
    ChangeValue newValue = generateValue(e);

    // if its an update, only include it if the value has changed:
    // what happens if we are clearing an attribute???
    if (newValue != null && e instanceof MatchedEntity) {
      MatchedEntity me = (MatchedEntity) e;
      Entity match = me.getMatch();

      Object currentValue = match.get(attribute);
      if (!isUpdated(currentValue, newValue.getValue()))
        return null;
    }

//    logger.trace("{}: identified {} as requiring update (should be {})", e.toString(), getAttribute(),
//        newValue.getValue().toString());  ///NPEs if conditional attr update doesn't apply

    return newValue;
  }

  /**
   * Work out if the new value is any different from the old value
   * 
   * @param currentValue
   * @param newValue
   * @return
   */
  private boolean isUpdated(Object currentValue, Object newValue) {
    if (currentValue != null) {
      if (currentValue instanceof String)
        if (caseSensitive) {
          if (currentValue.toString().equals(newValue.toString()))
            return false;
        }
        else
          if (currentValue.toString().toLowerCase().equals(newValue.toString().toLowerCase()))
            return false;

      if (currentValue instanceof Collection && newValue instanceof Collection) {
        Collection<?> cv = (Collection<?>) currentValue;
        Collection<?> nv = (Collection<?>) newValue;
        if (cv.size() == nv.size() && cv.containsAll(nv))
          return false;
      }
      // bitwise, numbers?
    }

    return true;
  }
}
