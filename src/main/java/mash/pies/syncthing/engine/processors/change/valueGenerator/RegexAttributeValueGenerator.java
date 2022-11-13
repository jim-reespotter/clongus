package mash.pies.syncthing.engine.processors.change.valueGenerator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeValue;

/**
 * generates target value for an attribute given a regex recipe and some source attributes
 * 
 */
public class RegexAttributeValueGenerator extends AttributeValueGenerator {

    private String[] sources;
    private String pattern;
    private String delimiter = ":";

    public String [] getSources() {return sources;}
    public void setSources(String [] sourceAttributes) {this.sources = sourceAttributes;}
    public String getPattern() {return pattern;}
    public void setPattern(String pattern) {
        this.pattern = pattern;
        String[] parts = pattern.split("/");
        this.p = Pattern.compile(parts[1]);
        this.replace = parts[2];
    }

    public String getDelimiter() {return delimiter;}
    public void setDelimiter(String delimiter) {this.delimiter = delimiter;}


    private String replace;
    private Pattern p;

    // if matched entitiy is available, can we get at matched.<attr> to get target
    // attr?
    @Override
    ChangeValue generateValue(Entity e) {
        String source = "";
        for (int i = 0; i < sources.length; i++) {
            Object val = e.get(sources[i]);
            if (val == null)
                throw new RuntimeException("empty source field " + sources[i] + " for " + e);
            source += val + delimiter; // null attributes?
        }
        source = source.substring(0, source.length() - 1);

        Matcher m = p.matcher(source);
        if (m.find()) {
            String result = m.replaceAll(replace);

            return new ChangeValue(getUpdateAction(), result);
        } else
            return null;
    }

}
