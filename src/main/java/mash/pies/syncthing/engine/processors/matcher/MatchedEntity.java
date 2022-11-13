package mash.pies.syncthing.engine.processors.matcher;

import mash.pies.syncthing.engine.processors.Entity;

/**
 * Extends an Entity to have awareness of the source/dest entity that it is
 * matched with
 *
 * should match be available in Map? yes
 */
public class MatchedEntity extends Entity {

  private MatchedEntity match;

  // flag to show if this is the source or target half of the match
  private boolean isSource;

  public MatchedEntity getMatch() {
    return this.match;
  }

  // called internally only (below)
  private MatchedEntity(Entity target, MatchedEntity matchedSource) {
    isSource = false;

    if (target instanceof MatchedEntity)
      new RuntimeException(
          "Trying to match an already matched entity - this really shouldn't happen");

    this.putAll(target);
    this.match = matchedSource;
  }

  public MatchedEntity(Entity source, Entity target) {
    isSource = true;
    if (source instanceof MatchedEntity || target instanceof MatchedEntity)
      new RuntimeException(
          "Trying to match an already matched entity - this really shouldn't happen");

    this.putAll(source);
//    this.setIdentifier(source.getIdentifier());

    this.match = new MatchedEntity(target, this);
  }

  /**
   * method to search by string, will match:
   * - source.xxx as attr XXX of this
   * - target.xxx as attr of match
   * - match.xxx as attr of match
   *
   * TO DO: matches are built by source first, so refine this so that
   * source.source.xxx knows which one source is
   * TO DO: add overrides to call source/target/match something different
   * TO DO: make entities themselves know if they are source/target?
   */
  public Object get(Object key) {
    String attrName = (String) key;
    if (attrName.startsWith("match."))
      return match.get(attrName.substring(6));

    if (attrName.startsWith("source.") && !isSource)
      return super.get(
          attrName.substring(7));

    if (attrName.startsWith("target.") && isSource)
      return match.get(
          attrName.substring(7));

    return super.get(attrName);
  }
}
