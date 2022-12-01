package mash.pies.syncthing.engine.processors.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResponse;
import org.ldaptive.SearchScope;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeCommandGenerator;
import mash.pies.syncthing.engine.processors.change.LdapObjectChangeCommandGenerator;


/**
 * 
 * TO DO:? Add a means of adding a Ldapconnection directly (rather than by
 * config?)
 */
public class LdapObjectQuery extends LdapQuery {

        //    private LdapConnectionConfig connection;
        private String subOU;
        private String filter;
        private SearchScope scope = SearchScope.SUBTREE;
        
    //    public LdapConnectionConfig getConnection() {return connection;}
    
        // rename baseDN? its not base, its relative...
        public String getSubOU() {return subOU;}
        public void setSubOU(String subOU) {this.subOU = subOU;}
        public String getFilter() {return filter;}
        public void setFilter(String filter) {this.filter = filter;}
        public String getScope() {return scope.toString();}
        public void setScope(String scope) {this.scope = SearchScope.valueOf(scope);}

//    static Logger logger = LogManager.getLogger();

    @Override
    protected Set<Entity> read(Map<String, String> params) throws LdapException {

        String filterString = "(&" + filter;
        for (String key : params.keySet())
            filterString += "(" + key + "=" + params.get(key) + ")";
        filterString += ")";

        debug("LDAP Object QUery - base: "+getSubOU() + "," + getConnection().getBaseDN()+"; filter "+ filterString+"; scope: "+scope.toString());
                
        SearchRequest searchRequest = SearchRequest.builder()
                .scope(scope) // TO DO: set this from config - SearchControls.OBJECT_SCOPE etc
                .dn(getSubOU() + "," + getConnection().getBaseDN())
                .filter(filterString)
                .build();

        SearchOperation search = new SearchOperation(getConnection().getConnectionFactory(), searchRequest);

        SearchResponse response = search.execute();
        Collection<LdapEntry> entries = response.getEntries();

        Set<Entity> entities = new HashSet<Entity>();

        for (LdapEntry entry : entries) {
            trace("Processing: "+entry.toString());
            Entity e = processRecord(entry);
            entities.add(e);
            trace("imported " + e.toString());
        }

        return entities;

    }

    // TO DO: remove subOU and connection baseDN from DN
    private Entity processRecord(LdapEntry entry) {

        Entity e = new Entity();

        Collection<LdapAttribute> attrs = entry.getAttributes();
        for (LdapAttribute attr : attrs) {
            Collection<String> sVals = attr.getStringValues();
            if (sVals.size() > 0)
                if (sVals.size() == 1)
                    e.put(attr.getName(), sVals.toArray()[0]);
                else
                    e.put(attr.getName(), sVals);
            else {
                Collection<byte[]> bVals = attr.getBinaryValues();
                if (bVals.size() > 0)
                    if (bVals.size() == 1)
                        e.put(attr.getName(), bVals.toArray()[0]);
                    else
                        e.put(attr.getName(), bVals);
            }
        }

        trace("imported "+e.toString());
        return e;
    }



    @Override
    public ChangeCommandGenerator<?> getChangeCommandGenerator(Map<String, String> params) {
        return new LdapObjectChangeCommandGenerator(this, params);
    }
}
