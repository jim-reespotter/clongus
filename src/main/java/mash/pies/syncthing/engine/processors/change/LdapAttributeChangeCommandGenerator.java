package mash.pies.syncthing.engine.processors.change;

import java.util.HashSet;
import java.util.Map;

import org.ldaptive.AttributeModification;
import org.ldaptive.LdapAttribute;
import org.ldaptive.ModifyOperation;
import org.ldaptive.ModifyRequest;
import org.ldaptive.AttributeModification.Type;
import org.ldaptive.handler.ResultPredicate;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.valueGenerator.AttributeValueGenerator.Condition;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;
import mash.pies.syncthing.engine.processors.query.LdapAttributeQuery;

public class LdapAttributeChangeCommandGenerator extends ChangeCommandGenerator <LdapAttributeQuery> {

    public LdapAttributeChangeCommandGenerator(LdapAttributeQuery q, Map <String, String> params) {
        super(q, params);
    }

    @Override
    public ChangeCommand buildCreateChange(Map <String, ChangeValue> changes) {
        return new LdapAttributeCreateChange (changes);
    }

    class LdapAttributeCreateChange extends LdapAttributeChange {

        private ModifyRequest req;

        LdapAttributeCreateChange (Map<String, ChangeValue> changeDetails) {
            
            HashSet<AttributeModification> mods = new HashSet<AttributeModification>();
            for (String key : changeDetails.keySet()) {
                LdapAttribute attribute = new LdapAttribute(key);
                attribute.addStringValues(changeDetails.get(key).toString());
                mods.add(new AttributeModification(Type.ADD, attribute));
            }
            req = ModifyRequest.builder()
                    .dn(getParams().get("target"))
                    .modifications(mods).build();
        }

        @Override
        public void invoke() throws Exception {
            ModifyOperation.builder()
                    .factory(getQuery().getConnection().getConnectionFactory())
                    .throwIf(ResultPredicate.NOT_SUCCESS)
                    .build()
                    .execute(req).isSuccess();
        }

        @Override
        public String toString() {
            String msg =  "Ldap object: "+req.getDn()+" - set attribute: ";
            AttributeModification mod = req.getModifications()[0];
            msg += mod.getAttribute().getName() + " => " + mod.getAttribute().getStringValue();
            return msg;
        }
    }

    @Override
    public ChangeCommand buildUpdateChange(MatchedEntity me, Map <String, ChangeValue> changes) {
        return new LdapAttributeRemoveChange (changes);
    }

    @Override
    public ChangeCommand buildRemoveChange(Entity e) {
        return new LdapAttributeRemoveChange (tvg.generateChange(e,Condition.REMOVE));
    }

    class LdapAttributeRemoveChange extends LdapAttributeChange {

        private ModifyRequest req;

        LdapAttributeRemoveChange (Map<String, ChangeValue> changeDetails) {
            
            HashSet<AttributeModification> mods = new HashSet<AttributeModification>();
            for (String key : changeDetails.keySet()) {
                LdapAttribute attribute = new LdapAttribute(key);
                attribute.addStringValues(changeDetails.get(key).toString());
                mods.add(new AttributeModification(Type.DELETE, attribute));
            }
            req = ModifyRequest.builder()
                    .dn(getParams().get("target"))
                    .modifications(mods).build();
        }

        @Override
        public void invoke() throws Exception {
            ModifyOperation.builder()
                    .factory(getQuery().getConnection().getConnectionFactory())
                    .throwIf(ResultPredicate.NOT_SUCCESS)
                    .build()
                    .execute(req).isSuccess();
        }

        @Override
        public String toString() {
            String msg = "LDAP object: "+req.getDn() +" - remove attribute: ";
            AttributeModification mod = req.getModifications()[0];
            msg+=mod.getAttribute().getName() +" => " + mod.getAttribute().getStringValue();
            return msg;
        }
    }

    private abstract class LdapAttributeChange implements ChangeCommand {

        public String getDescription() {return "yup";}
    }
}
