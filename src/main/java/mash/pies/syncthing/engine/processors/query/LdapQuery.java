package mash.pies.syncthing.engine.processors.query;

import org.ldaptive.schema.AttributeType;
import org.ldaptive.transcode.ByteArrayValueTranscoder;
import org.ldaptive.transcode.DoubleValueTranscoder;
import org.ldaptive.transcode.GeneralizedTimeValueTranscoder;
import org.ldaptive.transcode.IntegerValueTranscoder;
import org.ldaptive.transcode.StringValueTranscoder;
import org.ldaptive.transcode.UUIDValueTranscoder;
import org.ldaptive.transcode.ValueTranscoder;

import mash.pies.syncthing.engine.processors.connection.LdapConnection;

/**
 * Base class for LdapQueries.
 */
public abstract class LdapQuery extends Query  {
    
    private LdapConnection connection;
    
    public LdapConnection getConnection() {return connection;}
    public void setConnection(LdapConnection connection) {this.connection = connection;}

    /**
     * Sets datatypes of ldap attribute values
     * @param attribute
     * @return
     */
    public ValueTranscoder<?> getTranscoder(String attribute) {
        AttributeType at = getConnection().getAttributeType(attribute);
        switch (at.getSyntaxOID()) {
            case "2.5.5.11":
                return new GeneralizedTimeValueTranscoder();    
            case "2.5.5.9":
            case "1.3.6.1.4.1.1466.115.121.1.27":
                return new IntegerValueTranscoder();
            case "2.5.5.16":
                return new DoubleValueTranscoder();
            case "1.2.3.4": //....
                return new UUIDValueTranscoder();
            case "2.5.5.10":  // password, also objectGUID
            case "2.5.5.17":  //objectSID
                return new ByteArrayValueTranscoder();
            case "2.5.5.12":               // bog standard string (MS):
            case "1.3.6.1.4.1.1466.115.121.1.15":             // bog standard string:
            case "2.5.5.2": // objectclass
            case "1.3.6.1.4.1.1466.115.121.1.38":
            case "1.3.6.1.4.1.1466.115.121.1.40": // password?
            case "2.5.5.1":         // DN:
            default:
                return new StringValueTranscoder();
        }
    }

    public boolean isMultiValued(String attribute) {
        AttributeType at = getConnection().getAttributeType(attribute);
        return !at.isSingleValued();
    }   
}
