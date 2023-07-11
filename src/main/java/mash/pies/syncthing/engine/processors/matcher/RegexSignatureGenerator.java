package mash.pies.syncthing.engine.processors.matcher;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.util.RegexSubstitutor;

/**
 * Generates a string to be used for matching things to other things.
 * String is generated from the thing's attributes using reguar expression capture groups
 */
public class RegexSignatureGenerator implements SignatureGenerator {

    private RegexSubstitutor regex = new RegexSubstitutor();
    
    public String [] getAttributes() {return regex.getAttributes();}
    public void setAttributes(String [] attributes) {regex.setAttributes(attributes);}
    
    public String getDelimiter() {return regex.getDelimiter();}
    public void setDelimiter(String delimiter) {regex.setDelimiter(delimiter);}
    
    public String getPattern() {return regex.getPattern();}
    public void setPattern(String pattern) {regex.setPattern(pattern);}

    public String getSubstitution() {return regex.getSubstitution();}
    public void setSubstitution(String substitution) {regex.setSubstitution(substitution);}

    public boolean getCaseSensitive() {return regex.getCaseSensitive();}
    public void setCaseSensitive(boolean caseSensitive) {regex.setCaseSensitive(caseSensitive);;}
    
    @Override
    public Signature getSignature(Entity e) {

        String signature = regex.getSubstitution(e);
        if (signature != null)
            return new StringSignature (signature);
        else
            return null;
    }
}
