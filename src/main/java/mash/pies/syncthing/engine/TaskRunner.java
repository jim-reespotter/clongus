package mash.pies.syncthing.engine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import mash.pies.syncthing.engine.processors.change.ChangeCommand;
import mash.pies.syncthing.engine.processors.task.TaskProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Sample code to run a task.... */
public class TaskRunner {

  static Logger logger = LogManager.getLogger();
  private TaskProcessor tp;

  public TaskRunner(TaskProcessor tp) throws Exception {

    this.tp = tp;
  }

  public Collection<ChangeCommand> createChanges()
      throws Exception {

    logger.trace("Starting taskrunner: {}", tp.getName());

    Set<ChangeCommand> changes = new HashSet<ChangeCommand>();
    changes.addAll(tp.generateChanges());

    return changes;
  }

  public void invoke(ChangeCommand c) {

    try {
      c.invoke();
      logger.trace("completed.");
    } catch (Exception e) {
      logger.trace("failed " + e.getMessage());
    }
  }
}
