package mash.pies.syncthing.engine.processors.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.LogBase;
import mash.pies.syncthing.engine.processors.ProcessorBase;

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

    return matches;
  }
}
