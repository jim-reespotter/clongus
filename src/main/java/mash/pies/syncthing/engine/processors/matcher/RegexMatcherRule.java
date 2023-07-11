package mash.pies.syncthing.engine.processors.matcher;

/**
 * Processes source and target entity collections, matches against a single
 * given rule
 */
public class RegexMatcherRule extends MatcherRule<RegexSignatureGenerator> {

  public RegexMatcherRule() {
    super(new RegexSignatureGenerator(), new RegexSignatureGenerator());
  }
  
  public RegexSignatureGenerator getSourceSignature() {return sourceSignature;}
  public RegexMatcherRule withSourceSignature(RegexSignatureGenerator rsc) {this.sourceSignature = rsc; return this;}
  public RegexSignatureGenerator getTargetSignature() {return targetSignature;}
  public RegexMatcherRule withTargetSignature(RegexSignatureGenerator rsc) {this.targetSignature = rsc; return this;}

  public boolean getCaseSensitive() {return getSourceSignature().getCaseSensitive();}
  public void setCaseSensitive(boolean caseSensitive) {
    getSourceSignature().setCaseSensitive(caseSensitive);
    getTargetSignature().setCaseSensitive(caseSensitive);
  }
  protected boolean isMatch(Signature sourceSig, Signature targetSig) {
    return sourceSig
        .getSignature()
        .toString()
        .equals(targetSig.getSignature().toString());
  }
}