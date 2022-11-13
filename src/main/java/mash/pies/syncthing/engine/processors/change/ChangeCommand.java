package mash.pies.syncthing.engine.processors.change;


/**
 * Command pattern definition to produce a runnable change
 * Implementors will do this in a datasource specific way
 */
public interface ChangeCommand {

  void invoke() throws Exception;

  /**
   * This is whether the change is a create, change or remove.
   * 
   * For a change, the specifics of the change (from @getChangeDetails) may be adding/removing/appending values - see @ChangeValue
   * @return
   */
//  String getAction();

  String toString();
}
