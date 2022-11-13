package mash.pies.syncthing.engine.processors.query;

import java.util.List;
import java.util.Map;
import java.util.Set;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.ProcessorBase;
import mash.pies.syncthing.engine.processors.change.ChangeCommandGenerator;
import mash.pies.syncthing.engine.processors.change.valueGenerator.AttributeValueGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for all queries
 */

public abstract class Query extends ProcessorBase {
  
  static Logger logger = LogManager.getLogger();

  protected abstract Set<Entity> read(Map<String, String> params)
      throws Exception;

  protected abstract ChangeCommandGenerator<?> getChangeCommandGenerator(
      Map<String, String> params);

  public ChangeCommandGenerator<?> getChangeCommandGenerator(
      List<AttributeValueGenerator> attributeDefinitions,
      Map<String, String> params) {
    ChangeCommandGenerator<?> ccg = getChangeCommandGenerator(params);
    ccg.setAttributeDefinitions(attributeDefinitions);
    return ccg;
  }

  
}
