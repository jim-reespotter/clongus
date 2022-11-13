package mash.pies.syncthing.engine.processors.change.valueGenerator;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeValue;

/**
 * Generat a random value for the given attribute
 * consider~:
 * https://stackoverflow.com/questions/22115/using-regex-to-generate-strings-rather-than-match-them
 */
public class RandomAttributeValueGenerator extends AttributeValueGenerator {

    private int length;
    private String[] characters;

    public int getLength() {return length;}
    public void setLength(int length) {this.length = length;}
    public String [] getCharacters() {return characters;}
    public void setCharacters(String[] characters) {this.characters = characters;}


    @Override
    ChangeValue generateValue(Entity e) {
        String result = "";

        for (int i = 0; i < length; i++)
            result += characters[i % characters.length]
                    .charAt((int) ((characters[i % characters.length].length()) * Math.random()));
        return new ChangeValue(getUpdateAction(), result);
    }
}
