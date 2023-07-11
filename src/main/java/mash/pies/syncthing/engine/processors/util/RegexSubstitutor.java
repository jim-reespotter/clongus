package mash.pies.syncthing.engine.processors.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Regex substitution swiss army knife.
 * 
 * A regex pattern matcher is made from the given regular expression pattern, along with a substitution pattern)
 * passing in an entity, it will concatenate the specified attribute values (with the specified separator), apply
 * the regex, and return the substitution.
 * 
 * If a requeted attribute is not present in the entity, an empty string is used instead.
 * 
 * See https://www.freeformatter.com/java-regex-tester.html for testing - Java regex is weird.
 */
public class RegexSubstitutor {
    
    private Pattern pattern;
    private String[] attributes;
    private String substitution;
    private String delimiter;
    private boolean caseSensitive = true;

    public RegexSubstitutor() {
        // set some defaults...
        pattern = Pattern.compile("(.+)");
        substitution = "$1";
        delimiter = ":";
    }
    
    public String getPattern() {return pattern.pattern();}
    public void setPattern(String pattern) {this.pattern = Pattern.compile(pattern);}
    public String [] getAttributes() {return attributes;}
    public void setAttributes(String [] attributes) {this.attributes = attributes;}
    public String getSubstitution() {return substitution;}
    public void setSubstitution(String substitution) {this.substitution = substitution;}
    public String getDelimiter() {return delimiter;}
    public void setDelimiter(String delimiter) {this.delimiter = delimiter;}
    public boolean getCaseSensitive() {return caseSensitive;}
    public void setCaseSensitive(boolean caseSensitive) {this.caseSensitive = caseSensitive;}

    /**
     * pass in an entity; the specified attribute values will be concatentated (separated by specified delimiter)
     * then regex'd and substituted.
     * If no match is found or the regex fails, it returns null.
     * @param data
     * @return
     */
    public String getSubstitution (Map <String, ?> data) {

        String source = "";
        for (int i = 0; i < attributes.length; i++) {
            Object val = data.get(attributes[i]);
            if (val == null)
                val = "";
            source += val + delimiter;
        }
        source=source.substring(0, source.length()-delimiter.length());
        
        if (!caseSensitive)
            source = source.toLowerCase();

        Matcher m = pattern.matcher(source);
        if (m.find()) 
            return m.replaceAll(substitution);
        else
            return null;
    }
}
