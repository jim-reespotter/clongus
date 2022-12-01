package mash.pies.syncthing.engine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import mash.pies.syncthing.engine.processors.change.ChangeCommand;
import mash.pies.syncthing.engine.processors.task.TaskProcessor;

/** Sample code to run a task.... not sure we still need this? Move into TaskProcessor? SyncRunner?*/
public class TaskRunner {

  private TaskProcessor tp;

  public TaskRunner(TaskProcessor tp) throws Exception {

    this.tp = tp;
  }

  public Collection<ChangeCommand> createChanges()
      throws Exception {

    Set<ChangeCommand> changes = new HashSet<ChangeCommand>();
    changes.addAll(tp.generateChanges());

    return changes;
  }
}
