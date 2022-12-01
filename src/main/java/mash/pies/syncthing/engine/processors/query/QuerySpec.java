package mash.pies.syncthing.engine.processors.query;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.data.mongodb.core.mapping.DBRef;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.LogBase;

public class QuerySpec extends LogBase {

    @DBRef
    private Query query;

    /**
     * an appropriate filter to be sent to the query (depends on Query type)
     * This takes the form of KVPs in the Task (contains a list of the key names,
     * use values in task)
     */
    private Collection<String> send;

    /**
     * A means of renaming attributes that are released.
     * 
     * A Map of:
     * - key: name of the attribute to release
     * - value: original name of the attribute
     */
    private Map<String, String> release;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public Collection<String> getSend() {
        return send;
    }

    public void setSend(Collection<String> send) {
        this.send = send;
    }

    public Map<String, String> getRelease() {
        return release;
    }

    public void setRelease (Map<String, String> release) {this.release = release;}

    public Collection<Entity> execute() throws Exception {
        return execute(new HashMap<String, String>());
    }

    public Collection<Entity> execute(Map<String, String> filters) throws Exception {

        Map<String, String> sendFilter = new HashMap<String, String>();
        if (send != null) //think this should be a combination of filters and send?
            for (String key : send)
                if (filters.containsKey(key)) {
                    sendFilter.put(key, filters.get(key));
                    trace("Applying filter: "+key+" => "+filters.get(key));
                }

        Collection<Entity> qResult = getQuery().read(sendFilter);

        if (release != null) {
            trace("Renaming attributes:");
            Set<Entity> output = new HashSet<Entity>();

            // make a new entity here:
            for (Entity e : qResult) {
                Entity newEntity = new Entity();
                for (String t : release.keySet()) {
                    Object val = release.get(t);
                    newEntity.put(t, e.get(val));
                    trace("setting attribute "+t+" => "+e.get(val));
                }

                output.add(newEntity);
            }
            return output;
        } else
            return qResult;
    }
}
