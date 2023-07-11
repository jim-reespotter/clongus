package mash.pies.syncthing.engine.processors.change.valueGenerator;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.util.RegexSubstitutor;

/**
 * generates target value for an attribute given a regex recipe and some source attributes
 * 
 */
public class RegexAttributeValueGenerator extends AttributeValueGenerator {

//    private String[] sources;
//    private String pattern;
//    private String delimiter = ":";
    private RegexSubstitutor regex = new RegexSubstitutor();

    public String [] getSources() {return regex.getAttributes();}
    public void setSources(String [] sourceAttributes) {regex.setAttributes(sourceAttributes);}
    public String getPattern() {return regex.getPattern();}
    public void setPattern(String pattern) {regex.setPattern(pattern);}
    public String getSubstitution() {return regex.getSubstitution();}
    public void setSubstitution(String substitution) {regex.setSubstitution(substitution);}
    public String getDelimiter() {return regex.getDelimiter();}
    public void setDelimiter(String delimiter) {regex.setDelimiter(delimiter);}
    public boolean getCaseSensitive() {return regex.getCaseSensitive();}
    public void setCaseSensitive(boolean caseSensitive) {regex.setCaseSensitive(caseSensitive);}


    // if matched entitiy is available, can we get at matched.<attr> to get target
    // attr?
    @Override
    ChangedValue generateValue(Entity e) {
        String newValue = regex.getSubstitution(e);
        if (newValue != null)
            return new ChangedValue(newValue);
        else
            return null;
    }
}
