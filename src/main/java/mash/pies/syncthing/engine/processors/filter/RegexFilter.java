package mash.pies.syncthing.engine.processors.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mash.pies.syncthing.engine.processors.Entity;

public class RegexFilter extends FilterRule {
    
    // somehow wire in RegexSubstitutor...?
    private String pattern;
    private boolean caseSensitive = true;
    private String delimiter = ":";
    private boolean not = false;

    private Pattern p;

    public String getPattern() {return pattern;}
    public void setPattern(String pattern) {
        this.pattern = pattern;
        p = Pattern.compile(pattern);
    }
    public boolean getCaseSensitive() {return caseSensitive;}
    public void setCaseSensitive(boolean caseSenstive) {this.caseSensitive = caseSenstive;}

    public String getDelimiter() {return delimiter;}
    public void setDelimiter(String delimiter) {this.delimiter = delimiter;}

    public boolean getNot() {return not;}
    public void setNot(boolean not) {this.not = not;}


    public boolean allow(Entity e) {
        String source = "";
        for (String attribute : getAttributes()) {
            Object val = e.get(attribute);
            if (val == null)
                return false;
            source += val + delimiter;
        }
        source=source.substring(0, source.length()-1);
        
        if (!caseSensitive)
            source = source.toLowerCase();

        Matcher m = p.matcher(source);
        boolean result = m.matches() ^ not; 
        if (result)
            trace("Allowed "+e);
        else
            trace("Blocked "+e);

        return result;
    } 
}
