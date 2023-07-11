package mash.pies.syncthing.engine.processors.connection;

import java.util.HashMap;
import java.util.Map;

import org.ldaptive.BindConnectionInitializer;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.LdapException;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchScope;
//import org.ldaptive.schema.SchemaFactory;
import org.ldaptive.ad.schema.SchemaFactory;
import org.ldaptive.schema.AttributeType;
import org.ldaptive.schema.Schema;
import org.ldaptive.ssl.AllowAnyTrustManager;
import org.ldaptive.ssl.SslConfig;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * A connection to an LDAP directory
 */
public class LdapConnection extends Connection {
    
    private ConnectionFactory cf;

    private String url;
    private String cert;
    private PasswordCredential credential; //  = new PasswordCredential();
    private String baseDN;

    public String getUrl() {return url;}
    public void setUrl(String url) {this.url = url;}

    public String getCert() {return cert;}
    public void setCert(String cert) {this.cert = cert;}
    public PasswordCredential getCredential() {return credential;}
    public void setCredential(PasswordCredential creds) {this.credential = creds;}
    public String getBaseDN() {return baseDN;}
    public void setBaseDN (String baseDN) {this.baseDN = baseDN;}

    public String getType() {return "LDAP connection";}
    
    public ConnectionFactory getConnectionFactory() {
        
        if (cf == null) {
            DefaultConnectionFactory.Builder b = DefaultConnectionFactory.builder();

            ConnectionConfig.Builder cb = ConnectionConfig.builder();
            cb.url(url);
            cb.useStartTLS(false);
            cb.sslConfig(new SslConfig(new AllowAnyTrustManager()));
            
            if (credential != null) {
                BindConnectionInitializer.Builder bind = BindConnectionInitializer.builder();
                bind.dn(credential.getUsername());
                bind.credential(credential.getPassword());
                cb.connectionInitializers(bind.build());
            }
            else {} // anonymous bind?

            b.config(cb.build());

            cf = b.build();

            readSchema();
        }
        
        // make ldaptive shut up... maybe??
        Logger logger;
        logger = (Logger)LoggerFactory.getLogger("org.ldaptive");
        logger.setLevel(Level.INFO);
        logger = (Logger)LoggerFactory.getLogger("io.netty");
        logger.setLevel(Level.INFO);
    
        return cf;
    }

    private Map<String,AttributeType> attributesTypes = new HashMap<String,AttributeType>();

    public AttributeType getAttributeType(String name) {return attributesTypes.get(name);}

    private void readSchema() {


        SearchRequest sr = SearchRequest.builder()
            .scope(SearchScope.SUBTREE)
            .dn("cn=schema,cn=configuration,dc=reespotter,dc=home")
            .filter("(objectClass=*)")
            .build();

        SearchOperation sOp = new SearchOperation(getConnectionFactory(), sr);
        
        try {
            Schema sch = SchemaFactory.createSchema(sOp.execute());
            for (AttributeType at : sch.getAttributeTypes())
                for (String name : at.getNames())
                    attributesTypes.put(name, at);

        } catch (LdapException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}
