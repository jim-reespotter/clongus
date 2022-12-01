package mash.pies.syncthing.engine.processors.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeCommand;
import mash.pies.syncthing.engine.processors.query.QuerySpec;

public class ForEachTaskProcessor extends TaskProcessor {
    
    private QuerySpec forEach;
    private List<TaskProcessor> tasks = new ArrayList<TaskProcessor>();

    public QuerySpec getForEach() {return forEach;}
    public void setForEach(QuerySpec forEach) {this.forEach=forEach;}
    
    public List<TaskProcessor> getSubtasks() {return tasks;}
    public void setSubtasks(List<TaskProcessor> tasks) {this.tasks = tasks;}

    @Override
    public Collection<ChangeCommand> generateChanges() throws Exception {
        return generateChanges(null);
    }

    @Override
    public Collection<ChangeCommand> generateChanges(Map<String, String> params) throws Exception {

        if (params == null)
            params = new HashMap<String,String> ();

        Set<ChangeCommand> changes = new HashSet<ChangeCommand>();
        Collection<Entity> forEachEntities = forEach.execute(params);
        debug("Read "+forEachEntities.size()+" entities, running a sync task for each");

        for (Entity forEachEntity : forEachEntities) {
            info("running sync task for "+forEachEntity.toString());
    
            Map <String,String> forEachParams = new HashMap<String,String> (params);
            for (String param : forEachEntity.keySet())
                forEachParams.put(param,forEachEntity.get(param).toString());
    
            for (TaskProcessor task : tasks) 
                changes.addAll(task.generateChanges(forEachParams));
        
        }
        return changes;
    }
}
