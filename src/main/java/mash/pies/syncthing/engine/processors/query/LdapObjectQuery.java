package mash.pies.syncthing.engine.processors.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResponse;
import org.ldaptive.SearchScope;
import org.ldaptive.control.PagedResultsControl;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeCommandGenerator;
import mash.pies.syncthing.engine.processors.change.LdapObjectChangeCommandGenerator;


/**
 * 
 * TO DO:? Add a means of adding a Ldapconnection directly (rather than by
 * config?)
 */
public class LdapObjectQuery extends LdapQuery {

        private String subOU;
        private String filter;
        private SearchScope scope = SearchScope.SUBTREE;
        
        public String getSubOU() {return subOU;}
        public void setSubOU(String subOU) {this.subOU = subOU;}
        public String getFilter() {return filter;}
        public void setFilter(String filter) {this.filter = filter;}
        public String getScope() {return scope.toString();}
        public void setScope(String scope) {this.scope = SearchScope.valueOf(scope);}

    @Override
    protected Set<Entity> read(Map<String, String> params) throws LdapException {

        String filterString = "(&" + filter;
        for (String key : params.keySet())
            filterString += "(" + key + "=" + params.get(key) + ")";
        filterString += ")";

        debug("LDAP Object QUery - base: "+(getSubOU() != null ? getSubOU() + "," : "") + getConnection().getBaseDN()+"; filter "+ filterString+"; scope: "+scope.toString());
                
        String ou;
        if (getSubOU() == null)
            ou=getConnection().getBaseDN();
        else
            ou=getSubOU()+","+getConnection().getBaseDN();
            
        SearchRequest searchRequest = SearchRequest.builder()
                .scope(scope) 
                .dn(ou)
                .filter(filterString)
                .build();

        SearchOperation search = new SearchOperation(getConnection().getConnectionFactory(), searchRequest);

//  non-paged:        
        SearchResponse response = search.execute();
        Collection<LdapEntry> entries = response.getEntries();



/* paged - doesn't work (against my samba AD anyway)
        PagedResultsControl prc = new PagedResultsControl(8);

        searchRequest.setControls(prc);
        Collection<LdapEntry> entries = new HashSet<LdapEntry>();
        byte[] cookie = null;
        do {
            prc.setCookie(cookie);
            SearchResponse res = search.execute(searchRequest);
            entries.addAll(res.getEntries());
            
            cookie = null;
            PagedResultsControl ctl = (PagedResultsControl) res.getControl(PagedResultsControl.OID);
            if (ctl != null) {
                if (ctl.getCookie() != null && ctl.getCookie().length > 0) {
                    cookie = ctl.getCookie();
                }
            }
        } while (cookie != null);
*/

        Set<Entity> entities = new HashSet<Entity>();

        for (LdapEntry entry : entries) {
            trace("Processing: "+entry.toString());
            Entity e = processRecord(entry);
            entities.add(e);
            trace("imported " + e.toString());
        }

        return entities;

    }

    private Entity processRecord(LdapEntry entry) {

        Entity e = new Entity();

        for (String attrName : entry.getAttributeNames())
            if (isMultiValued(attrName))
                e.put(attrName, entry.getAttribute(attrName).getValues(getTranscoder(attrName).decoder()));
            else
                e.put(attrName, entry.getAttribute(attrName).getValue(getTranscoder(attrName).decoder()));

        trace("imported "+e.toString());
        return e;
    }

    @Override
    public ChangeCommandGenerator<?> getChangeCommandGenerator(Map<String, String> params) {
        return new LdapObjectChangeCommandGenerator(this, params);
    }
}
