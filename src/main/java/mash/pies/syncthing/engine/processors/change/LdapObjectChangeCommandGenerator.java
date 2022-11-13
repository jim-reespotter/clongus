package mash.pies.syncthing.engine.processors.change;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;
import mash.pies.syncthing.engine.processors.query.LdapObjectQuery;
import org.ldaptive.AddOperation;
import org.ldaptive.AddRequest;
import org.ldaptive.AttributeModification;
import org.ldaptive.DeleteOperation;
import org.ldaptive.DeleteRequest;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapException;
import org.ldaptive.ModifyDnOperation;
import org.ldaptive.ModifyDnRequest;
import org.ldaptive.ModifyOperation;
import org.ldaptive.ModifyRequest;
import org.ldaptive.handler.ResultPredicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A processor to create LDAP Object change objects - create, update and delete
 */
public class LdapObjectChangeCommandGenerator extends ChangeCommandGenerator<LdapObjectQuery> {

  static Logger logger = LogManager.getLogger();

  public LdapObjectChangeCommandGenerator(
      LdapObjectQuery q,
      Map<String, String> params) {
    super(q, params);
  }

  /***************** CREATE object requests ******************/

  public ChangeCommand buildCreateChange(Map<String, ChangeValue> changes) {
    return new LdapObjectCreateChange(changes);
  }

  class LdapObjectCreateChange extends LdapObjectChange {

    AddRequest req;

    LdapObjectCreateChange(Map<String, ChangeValue> changeDetails) {
      super(changeDetails);

      AddRequest.Builder builder = AddRequest
          .builder()
          .dn(
              (String) changeDetails.get("distinguishedName").getValue() +
                  "," +
                  getQuery().getConnection().getBaseDN());

      HashSet<LdapAttribute> attrs = new HashSet<LdapAttribute>();

      for (String attrName : changeDetails.keySet()) {
        LdapAttribute attribute = new LdapAttribute(attrName);
        Object value = changeDetails.get(attrName).getValue();

        if (value instanceof Collection) {
          Collection<?> coll = (Collection<?>) value;
          if (coll.size() > 0)
            if (coll.toArray()[0] instanceof String)
              attribute.addStringValues((Collection<String>) coll);
        } else if (value instanceof String && !attrName.equals("distinguishedName"))
          attribute.addStringValues((String) value);

        if (!attrName.equals("distinguishedName"))
          attrs.add(attribute);
      }

      builder.attributes(attrs);
      req = builder.build();
    }

    @Override
    public void invoke() throws LdapException {
      AddOperation ao = AddOperation
          .builder()
          .factory(getQuery().getConnection().getConnectionFactory())
          .throwIf(ResultPredicate.NOT_SUCCESS)
          .build();
      ao.execute(req).isSuccess();
    }

    @Override
    public String toString() {
      String msg = "Create: "+req.getDn();
      msg += " (Attributes: ";
      for (LdapAttribute attr : req.getAttributes())
        msg += 
            attr.getName() + " = "+
            attr.getStringValue() +"; ";
      msg+=")";

      return msg;
    }
  }

  /*****
   * UPDATE object requests (modify attributes + modify DN all rolled into one)
   ********/
  @Override
  public ChangeCommand buildUpdateChange(
      MatchedEntity me,
      Map<String, ChangeValue> changes) {
    LdapObjectUpdateChange change = new LdapObjectUpdateChange(me, changes);

    if (change.req == null && change.dnReq == null) {
      logger.trace("no change required for {}", me.toString());
      return null;
    } else
      return change;
  }

  // need to add in LDAP RENAME for changes to cn... can possibly fdo this by changeing dn?
  private class LdapObjectUpdateChange extends LdapObjectChange {

    ModifyRequest req;
    ModifyDnRequest dnReq;

    MatchedEntity me;

    LdapObjectUpdateChange(MatchedEntity me, Map<String, ChangeValue> changeDetails) {
      super(changeDetails);
      this.me = me;

      Entity match = me.getMatch();
      ModifyRequest.Builder builder = ModifyRequest
          .builder()
          .dn(match.get("distinguishedName").toString());

      String changeDN = changeDetails.get("distinguishedName").getValue().toString() +
          "," +
          getQuery().getConnection().getBaseDN();

      String targetDN = match.get("distinguishedName").toString();

      if (changeDN.equals(targetDN)) {
        changeDetails.remove("distinguishedName");
        logger.trace("removed potential change of DN");
      } else {
        ModifyDnRequest.Builder dnBuilder = ModifyDnRequest
            .builder()
            .oldDN(targetDN)
            .newRDN(changeDN.substring(0, changeDN.indexOf(",")))
            .superior(changeDN.substring(changeDN.indexOf(",") + 1))
            .delete(true);
        dnReq = dnBuilder.build();
      }

      HashSet<AttributeModification> attrs = new HashSet<AttributeModification>();

      for (String attrName : changeDetails.keySet()) {
        LdapAttribute attribute = new LdapAttribute(attrName);
        Object value = changeDetails.get(attrName).getValue();
        if (value instanceof Collection) {
          Collection<?> coll = (Collection<?>) value;
          if (coll.size() > 0)
            if (coll.toArray()[0] instanceof String)
              attribute.addStringValues((Collection<String>) coll);
          // binary values...
        } else if (value instanceof String)
          if (!attrName.equals("distinguishedName"))
            attribute.addStringValues((String) value);

        if (!attrName.equals("distinguishedName"))
          attrs.add(
              new AttributeModification(
                  AttributeModification.Type.REPLACE,
                  attribute));
      }

      if (attrs.size() > 0) {
        builder.modifications(attrs);
        req = builder.build();
      }
    }

    @Override
    public void invoke() throws Exception {
      if (req != null) {
        ModifyOperation mo = ModifyOperation
            .builder()
            .factory(getQuery().getConnection().getConnectionFactory())
            .throwIf(ResultPredicate.NOT_SUCCESS)
            .build();
        mo.execute(req).isSuccess();
      }
      if (dnReq != null) {
        ModifyDnOperation mo = ModifyDnOperation
            .builder()
            .factory(getQuery().getConnection().getConnectionFactory())
            .throwIf(ResultPredicate.NOT_SUCCESS)
            .build();
        mo.execute(dnReq).isSuccess();
      }
    }

    @Override
    public String toString() {
      String msg = "Update: "+req.getDn();
      msg += " (Attributes: ";
      for (AttributeModification mod : req.getModifications())
        msg += 
            mod.getOperation().name() + " "+
            mod.getAttribute().getName() +"="+
            mod.getAttribute().getStringValue()+"; ";
      msg+=")";
      return msg;
    }
  }

  public ChangeCommand buildRemoveChange(Entity e) {
    return new LdapObjectRemoveChange(e);
  }

  private class LdapObjectRemoveChange extends LdapObjectChange {

    private DeleteRequest req;
    private Entity e;

    public LdapObjectRemoveChange(Entity e) {
      super(null);

      this.e = e;

      req = DeleteRequest
          .builder()
          .dn(e.get("distinguishedName").toString())
          .build();
    }

    @Override
    public void invoke() throws Exception {
      DeleteOperation dOp = DeleteOperation
          .builder()
          .factory(getQuery().getConnection().getConnectionFactory())
          .throwIf(ResultPredicate.NOT_SUCCESS)
          .build();
      dOp.execute(req);
    }

    @Override
    public String toString() {
      return "Remove: "+req.getDn();
    }
  }

  /**
   * Base class for LDAP changes... not sure how useful it is/might be...
   */
  private abstract class LdapObjectChange implements ChangeCommand {

    Map<String, ChangeValue> changeDetails;

    LdapObjectChange (Map<String, ChangeValue> changeDetails) {
      this.changeDetails = changeDetails;
    }
  }
}
