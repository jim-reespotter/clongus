package mash.pies.syncthing.engine.processors.task;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mash.pies.syncthing.engine.processors.ProcessorBase;
import mash.pies.syncthing.engine.processors.change.ChangeCommand;
import mash.pies.syncthing.engine.processors.util.Condition;

public abstract class TaskProcessor extends ProcessorBase {

    protected abstract Collection<ChangeCommand> process () throws Exception;
    protected abstract Collection<ChangeCommand> process (Map <String,String> params) throws Exception;

    public void closeConnections() {}

    public Collection<ChangeCommand> createChanges() throws Exception {

        Set<ChangeCommand> changes = new HashSet<ChangeCommand>();
        changes.addAll(process());

        return changes;
    }
}
