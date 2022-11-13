package mash.pies.syncthing.engine.processors.matcher;

/**
 * A matcher that matches on o specified attribute from the source entity being equal to a specifiec attribute from the target
 */
public class AttributeValueMatcherRule extends MatcherRule<AttributeValueSignatureGenerator> {

    private boolean caseSensitive = true;

    public AttributeValueMatcherRule() {
        super(new AttributeValueSignatureGenerator(), new AttributeValueSignatureGenerator());
    }

    public boolean isCaseSensitive() {return caseSensitive;}
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        if (sourceSignature != null) 
            sourceSignature.withCaseSensitive(caseSensitive);
        if (targetSignature != null)
            targetSignature.withCaseSensitive(caseSensitive);
    }
    
    public String getSourceAttribute() {
        return sourceSignature.getAttributeName();
    }
    public void setSourceAttribute(String sourceAttribute) {
        sourceSignature.withAttributeName(sourceAttribute);
    }

    public String getTargetAttribute() {
        return ((AttributeValueSignatureGenerator) targetSignature).getAttributeName();
    }

    public void setTargetAttribute(String targetAttribute) {
        targetSignature.withAttributeName(targetAttribute);
    }

    public AttributeValueMatcherRule withTargetAttribute (String targetAttribute) {
        targetSignature = new AttributeValueSignatureGenerator().withAttributeName(targetAttribute).withCaseSensitive(caseSensitive);
        return this;
    }



    @Override
    public boolean isMatch(Signature sourceSig, Signature targetSig) {
        
        return sourceSig.getSignature().equals(targetSig.getSignature());
    }
}
