package mash.pies.syncthing.engine.processors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;




/**
 * An entity represents a 'thing' - an entry in a data store.
 * Likely entities are users, groups, memberships, events, subscriptions...
 * 
 * Entities are a collection of key-value pairs.
 * 
 * Might work better as a map wrapper? Or interface?
 */
public class Entity extends HashMap <String, Object> {
    
    /*
    @Override
    public String toString() {
        return super.toString();
    }
    */
/*
    public String getDetails() {
        return super.toString();
    }
*/
    public boolean isCollection(String attrName) {
        return get(attrName) instanceof Collection<?>;
    }

    public Collection<?> getAsCollection(String attrName) {
        if (get(attrName) instanceof Collection<?>)
            return (Collection <?>) get(attrName);
        else {
            Collection <Object> c = new ArrayList<>();
            c.add(get(attrName));
            return c;
        }
    }
}
