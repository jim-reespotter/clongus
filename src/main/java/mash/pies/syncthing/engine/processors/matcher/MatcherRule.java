package mash.pies.syncthing.engine.processors.matcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.ProcessorBase;

/**
 * 
 */
public abstract class MatcherRule<T extends SignatureGenerator> extends ProcessorBase {

  protected T sourceSignature;
  protected T targetSignature;
    
  protected MatcherRule(T sourceSignature, T targetSignature) {
    this.sourceSignature = sourceSignature;
    this.targetSignature = targetSignature;
  }

  public T getSourceSignature() {return sourceSignature;}
  public void setSourceSignature(T sourceSig) {this.sourceSignature = sourceSig;}
  public T getTargetSignature() {return targetSignature;}
  public void setTargetSignature(T targetSig) {this.targetSignature = targetSig;}

//  static Logger logger = LogManager.getLogger();

    /**
   * retrieve a list of all UNIQUE signatures for our collection of given Entities
   * Any signature that are not unique are flagged with null
   * 
   * @param entities
   * @param sigGen
   * @return
   */
  private Map<Signature, Entity> getSignatures(
        Collection<Entity> entities,
        SignatureGenerator sigGen) {
  
    Map<Signature, Entity> result = new HashMap<Signature, Entity>();

    // calculate the signature for all the given entities
    for (Entity e : entities) {
      Signature sig = sigGen.getSignature(e);
      if (sig != null)
        if (!result.keySet().contains(sig))
          result.put(sig, e);
        else {
          result.put(sig, null); // we've already got an entity with the same sig - they need to be unique, so
        }                       // flag this with null value:
    } 

    // find all sigs with null value (null because they aren't unique) and remove
    // them
    Iterator<Signature> iter = result.keySet().iterator();
    while (iter.hasNext()) {
      Signature s = iter.next();
      if (result.get(s) == null)
        iter.remove();
      else
        getLogger().trace("Signature for "+ result.get(s) +" : "+s);
    }
    return result;
  }

  public Map<Signature, Entity> getSourceSignatures(Collection<Entity> entities) {

    return getSignatures(entities, sourceSignature);
  }

  public Map<Signature, Entity> getTargetSignatures(Collection<Entity> entities) {
    return getSignatures(entities, targetSignature);
  }
  /**
   * Find matches between source and target based on signatures (and match rule?)
   * Any matches found are added to a new collection and removed from the
   * source/target collections
   * 
   * @param sourceEntities
   * @param targetEntities
   * @return
   */
  public Collection<MatchedEntity> findMatches(Collection<Entity> sourceEntities, Collection<Entity> targetEntities) {
    
    Set<MatchedEntity> matches = new HashSet<MatchedEntity>();

    trace("Running matcher rule " + getName());
    trace("Source signatures:");
    Map<Signature, Entity> sourceSigMap = getSignatures(sourceEntities, sourceSignature);
    trace("target signatures:");
    Map<Signature, Entity> targetSigMap = getSignatures(targetEntities, targetSignature);

    Iterator<Signature> srcIter = sourceSigMap.keySet().iterator();
    while (srcIter.hasNext()) {
      Signature srcSig = srcIter.next();

      if (targetSigMap.containsKey(srcSig)) {
        debug("Found unique match: " + srcSig.getSignature());
        sourceEntities.remove(sourceSigMap.get(srcSig));
          targetEntities.remove(targetSigMap.get(srcSig));

          // add to matched:
          matches.add(
              new MatchedEntity(
                  sourceSigMap.get(srcSig),
                  targetSigMap.get(srcSig)
              )
          );

          // remove from maps:
          srcIter.remove();
          targetSigMap.remove(srcSig);
      }
/*
      //threads?
      Iterator<Signature> tgtIter = targetSigMap.keySet().iterator();
      while (tgtIter.hasNext()) {
        Signature tgtSig = tgtIter.next();

        if (isMatch(srcSig, tgtSig)) {
          debug("Found unique match: " + srcSig.getSignature());

          // remove from unmatched sets:
          sourceEntities.remove(sourceSigMap.get(srcSig));
          targetEntities.remove(targetSigMap.get(tgtSig));

          // add to matched:
          matches.add(
              new MatchedEntity(
                  sourceSigMap.get(srcSig),
                  targetSigMap.get(tgtSig)));

          // remove from maps:
          srcIter.remove();
          tgtIter.remove();
        }
      }
*/
    }

    return matches;
  }

  /**
   * place to make sure sigs are present (needed for YAML configs)
   * aargh.
   */
//  protected abstract void initialiseMatchers();
  /**
   * this will vary depending on type.... maybe box this up sepasrately somewhere?
   * 
   * @param sourceSig
   * @param targetSig
   * @return
   */
  protected abstract boolean isMatch(Signature sourceSig, Signature targetSig);
}
