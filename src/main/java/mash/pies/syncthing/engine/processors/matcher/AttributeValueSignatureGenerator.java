package mash.pies.syncthing.engine.processors.matcher;

import mash.pies.syncthing.engine.processors.Entity;

public class AttributeValueSignatureGenerator implements SignatureGenerator {
    // TO DO: rename to AttributeStringSignatureGenerator?... (or rework to be byte[]?? matcher?)

    private String attributeName;
    private boolean caseSensitive = true;

    public String getAttributeName() {return attributeName;}
    public void setAttributeName(String attributeName) {this.attributeName = attributeName;}
    public boolean isCaseSensitive() {return caseSensitive;}
    public void setCaseSensitive(boolean caseSensitive) {this.caseSensitive = caseSensitive;}

    @Override
    public StringSignature getSignature(Entity e) {
        if (e.get(attributeName) == null)
            return null;
        if (caseSensitive)
            return new StringSignature(e.get(attributeName).toString());
        else
            return new StringSignature(e.get(attributeName).toString().toLowerCase());
    }
}
