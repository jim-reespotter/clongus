package mash.pies.syncthing.engine.processors.task;

import java.util.Collection;
import java.util.Map;

import mash.pies.syncthing.engine.processors.ProcessorBase;
import mash.pies.syncthing.engine.processors.change.ChangeCommand;

public abstract class TaskProcessor extends ProcessorBase {

    public abstract Collection<ChangeCommand> generateChanges () throws Exception;
    public abstract Collection<ChangeCommand> generateChanges (Map <String,String> params) throws Exception;

}
