package mash.pies.syncthing.engine.processors.connection;

import org.ldaptive.BindConnectionInitializer;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.DefaultConnectionFactory;
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
    private PasswordCredential credential = new PasswordCredential();
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
            cf = DefaultConnectionFactory.builder()
            .config(ConnectionConfig.builder()
            .url(url)
            .useStartTLS(false)                 // TO DO: from config
            .sslConfig(new SslConfig(new AllowAnyTrustManager()))       // TO DO - certs!
            .connectionInitializers(BindConnectionInitializer.builder()
                    .dn(credential.getUsername())
                    .credential(credential.getPassword())
                    .build()
            )
            .build())
        .build();
        }
        
        // make ldaptive shut up...
        Logger logger;;
        logger = (Logger)LoggerFactory.getLogger("org.ldaptive");
        logger.setLevel(Level.INFO);
        logger = (Logger)LoggerFactory.getLogger("io.netty");
        logger.setLevel(Level.INFO);
        
        return cf;
    }
}
