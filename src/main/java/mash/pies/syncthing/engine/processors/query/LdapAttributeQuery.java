package mash.pies.syncthing.engine.processors.query;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResponse;
import org.ldaptive.SearchScope;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeCommandGenerator;
import mash.pies.syncthing.engine.processors.change.LdapAttributeChangeCommandGenerator;
public class LdapAttributeQuery extends LdapQuery {

    private String ou;
    private String attribute;
    
    public String getOu() {return ou;}
    public void setOu(String ou) {this.ou = ou;}
    public String getAttribute() {return attribute;}
    public void setAttribute(String attribute) {this.attribute = attribute;}

    @Override
    protected Set<Entity> read(Map<String, String> params) throws Exception {
        
        SearchRequest searchRequest = SearchRequest.builder()
                .scope(SearchScope.OBJECT)    
                .dn(params.get("dn"))
                .filter("(objectClass=*)")
            .build();

        SearchOperation search = new SearchOperation (getConnection().getConnectionFactory(), searchRequest);

        SearchResponse response = search.execute();
        LdapEntry[] entries = response.getEntries().toArray(new LdapEntry[0]);
        
        if (entries.length != 1) throw new RuntimeException("Found "+entries.length+" objects at dn="+params.get("dn")+" - this is not good. (Dies)");
            
        // need to return these as Entities to fit in with later processing...
        Set<Entity> values = new HashSet<Entity> ();
    
        LdapAttribute attribute = entries[0].getAttribute(getAttribute());

        if (attribute != null)
            for (String value : attribute.getStringValues()) {
                Entity e = new Entity();
                e.put(getAttribute(), value);
                values.add(e);
            }
            
        return values;
    }

    @Override
    protected ChangeCommandGenerator<?> getChangeCommandGenerator(Map <String, String> params) {
        return new LdapAttributeChangeCommandGenerator(this, params);
    }
    
}
