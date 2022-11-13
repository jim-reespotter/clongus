package mash.pies.syncthing.engine.processors.matcher;

import mash.pies.syncthing.engine.RegexSubstitutor;
import mash.pies.syncthing.engine.processors.Entity;

/**
 * Generates a string to be used for matching things to other things.
 * String is generated from the thing's attributes using reguar expression capture groups
 */
public class RegexSignatureGenerator implements SignatureGenerator {

    private String [] attributes = new String[0];
    private String delimiter = ":";
    private String pattern = "/(.*)/$1/"; //  "/(.*):?/$1/";
    private boolean caseSensitive = true;  // not sure about this...

    public String [] getAttributes() {return attributes;}
    public void setAttributes(String [] attributes) {this.attributes = attributes;}
    
    public String getDelimiter() {return delimiter;}
    public void setDelimiter(String delimiter) {this.delimiter = delimiter;}
    
    public String getPattern() {return pattern;}
    public void setPattern(String pattern) {this.pattern = pattern;}

    public boolean getCaseSensitive() {return caseSensitive;}
    public void setCaseSensitive(boolean caseSensitive) {this.caseSensitive = caseSensitive;}
    
    
    
    private RegexSubstitutor regex;

    @Override
    public Signature getSignature(Entity e) {

        if (regex == null) {
            String[] parts = pattern.split("/");
            regex = new RegexSubstitutor(parts[1], attributes, parts[2],delimiter, caseSensitive);
        }

        String signature = regex.getSubstitution(e);
        if (signature != null)
            return new StringSignature (signature);
        else
            return null;
    }
}
