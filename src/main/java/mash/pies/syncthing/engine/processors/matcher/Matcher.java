package mash.pies.syncthing.engine.processors.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mash.pies.syncthing.engine.processors.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Processes the list of matchers in order against the source and target
 * collections, returns a set of matches
 */
public class Matcher {

  static Logger logger = LogManager.getLogger();

  private List<MatcherRule<?>> matcherRules; // = new ArrayList<MatcherRule>();

  public Matcher() {this.matcherRules = new ArrayList<MatcherRule<?>>();}
  public Matcher(List<MatcherRule<?>> matcherRules) {this.matcherRules = matcherRules;}

  public List<MatcherRule<?>> getMatcherRules() {return matcherRules;}

  public Set<MatchedEntity> match(Collection<Entity> sourceEntities, Collection<Entity> targetEntities) {

    Set<MatchedEntity> matches = new HashSet<MatchedEntity>();

    for (MatcherRule<?> matcherRule : matcherRules)
      matches.addAll(matcherRule.findMatches(sourceEntities, targetEntities));

    return matches;
  }
}
