package mash.pies.syncthing.engine;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Regex substituion swiss army knife
 */
public class RegexSubstitutor {
    
    private Pattern pattern;
    private String[] fields;
    private String substitution;
    private String delimiter;
    private boolean caseSensitive = true;

    public RegexSubstitutor(String pattern, String [] fields, String substitution, String delimiter, boolean caseSensitive) {
        this.pattern = Pattern.compile (pattern);
        this.fields = fields;
        this.substitution = substitution; 
        this.delimiter = delimiter;
        this.caseSensitive = caseSensitive;
    }

    public String getSubstitution (Map <String, ?> data) {

        String source = "";
        for (int i = 0; i < fields.length; i++) {
            Object val = data.get(fields[i]);
            if (val == null)
                return null;
            source += val + delimiter;
        }
        source=source.substring(0, source.length()-1);
        
        if (!caseSensitive)
            source = source.toLowerCase();

        Matcher m = pattern.matcher(source);
        if (m.find()) 
            return m.replaceAll(substitution);
        else
            return null;
    }
}
