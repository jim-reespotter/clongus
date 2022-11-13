package mash.pies.syncthing.engine.processors;

import java.util.HashMap;

import mash.pies.syncthing.engine.RegexSubstitutor;

/**
 * An entity represents a 'thing' - an entry in a data store.
 * Likely entities are users, groups, memberships, events, subscriptions...
 * 
 * Entities are a collection of key-value pairs.
 * 
 * Might work better as a map wrapper? Or interface?
 */
public class Entity extends HashMap <String, Object> {
    
    transient private RegexSubstitutor rs;

    public void setRegexSubstitutor(RegexSubstitutor rs) {
        this.rs = rs;
    }
    // friendly name:
//    private String identifier;

//    public String getIdentifier() {return identifier;}
//    public void setIdentifier(String identifier) {this.identifier = identifier;}

    @Override
    public String toString() {
        if (rs == null)
            return super.toString();
        else
            try {  // arghh
                return rs.getSubstitution(this);
            }
            catch (Exception e) {
                return super.toString();
            }
    }

    public String getIdentifier() {
        return toString();
    }

    public String getDetails() {
        return super.toString();
    }
}
