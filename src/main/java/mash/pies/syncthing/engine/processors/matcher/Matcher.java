package mash.pies.syncthing.engine.processors.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.qos.logback.classic.Level;
import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.LogBase;

/**
 * Processes the list of matchers in order against the source and target
 * collections, returns a set of matches
 */
public class Matcher extends LogBase {

  //static Logger logger = LogManager.getLogger();

  private List<MatcherRule<?>> matcherRules; // = new ArrayList<MatcherRule>();

  public Matcher() {this.matcherRules = new ArrayList<MatcherRule<?>>();}
  public Matcher(List<MatcherRule<?>> matcherRules) {this.matcherRules = matcherRules;}

  public List<MatcherRule<?>> getMatcherRules() {return matcherRules;}

  public Set<MatchedEntity> match(Collection<Entity> sourceEntities, Collection<Entity> targetEntities) {

    Set<MatchedEntity> matches = new HashSet<MatchedEntity>();

    for (MatcherRule<?> matcherRule : matcherRules) {
      debug("Processing matcher "+matcherRule.getName()+":");

      Collection<MatchedEntity> matchedEntities = matcherRule.findMatches(sourceEntities, targetEntities);
      matches.addAll(matchedEntities);
      debug("made "+matchedEntities.size()+" matches, "+sourceEntities.size()+" source, "+targetEntities.size()+" targets remaining");
    }

    debug("Finished matching - "+matches.size()+" entities matched, leaving "+sourceEntities.size()+" sources and "+targetEntities.size()+" targets.");
    
    if (getLogger().getLevel() == Level.TRACE) {
      for (Entity src : sourceEntities)
        trace("Unmathced source: "+src.toString());
      for (Entity target : targetEntities)  
        trace("unmatched target: "+target.toString());
    }
      
    return matches;
  }
}
