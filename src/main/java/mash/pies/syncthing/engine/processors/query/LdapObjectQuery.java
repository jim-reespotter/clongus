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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * TO DO:? Add a means of adding a Ldapconnection directly (rather than by
 * config?)
 */
public class LdapObjectQuery extends LdapQuery {

        //    private LdapConnectionConfig connection;
        private String subOU;
        private String filter;
        private String scope;
        
    //    public LdapConnectionConfig getConnection() {return connection;}
    
        // rename baseDN? its not base, its relative...
        public String getSubOU() {return subOU;}
        public void setSubOU(String subOU) {this.subOU = subOU;}
        public String getFilter() {return filter;}
        public void setFilter(String filter) {this.filter = filter;}
        public String getScope() {return scope;}
        public void setScope(String scope) {this.scope = scope;}

    static Logger logger = LogManager.getLogger();

    @Override
    protected Set<Entity> read(Map<String, String> params) throws LdapException {

        String filterString = "(&" + filter;
        for (String key : params.keySet())
        filterString += "(" + key + "=" + params.get(key) + ")";
        filterString += ")";

        logger.debug("LDAP Object QUery - base: {}; filter {}; scope: SUB",
                getSubOU() + "," + getConnection().getBaseDN(), filterString);
        SearchRequest searchRequest = SearchRequest.builder()
                .scope(SearchScope.SUBTREE) // TO DO: set this from config - SearchControls.OBJECT_SCOPE etc
                .dn(getSubOU() + "," + getConnection().getBaseDN())
                .filter(filterString)
                .build();

        SearchOperation search = new SearchOperation(getConnection().getConnectionFactory(), searchRequest);

        SearchResponse response = search.execute();
        Collection<LdapEntry> entries = response.getEntries();

        Set<Entity> entities = new HashSet<Entity>();

        for (LdapEntry entry : entries)
            entities.add(processRecord(entry));

        return entities;

    }

    // TO DO: remove subOU and connection baseDN from DN
    private Entity processRecord(LdapEntry entry) {

        Entity e = new Entity();
//        e.setRegexSubstitutor(this.idRegex);

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

        return e;
    }



    @Override
    public ChangeCommandGenerator<?> getChangeCommandGenerator(Map<String, String> params) {
        return new LdapObjectChangeCommandGenerator(this, params);
    }
}
