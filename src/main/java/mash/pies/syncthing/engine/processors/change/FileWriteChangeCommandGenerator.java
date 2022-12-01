package mash.pies.syncthing.engine.processors.change;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.matcher.MatchedEntity;
import mash.pies.syncthing.engine.processors.query.FileWriteQuery;
import mash.pies.syncthing.engine.processors.query.CsvQuery.Field;

public class FileWriteChangeCommandGenerator extends ChangeCommandGenerator<FileWriteQuery> {
    
    public FileWriteChangeCommandGenerator(FileWriteQuery query, Map<String, String> params) {
        super(query, params);
        // TODO Auto-generated method stub
        /*
         * Lets use regex to sub out params - eg:   s/(.+):(.+):(.+):(.+)/add-adUser "$1" --samaccountname $2 --validUntil $4 < echo "$3"
         * delimiter = : (by default)
         * - for each change:
         * for each field in fields:
         * - find it in entity, process it to string, concatenate
         * - -> jim|--|mySamName|--|abcd1234|--|2024-01-01
         */
  //      return null;
    }

    @Override
    public ChangeCommand buildCreateChange(Map<String, ChangeValue> changes) throws SQLException {
        if (getQuery().getCreatePattern() != null)
            return new Change(changes, getQuery().getCreatePattern());
        else
            return null;
    }

    @Override
    public ChangeCommand buildUpdateChange(MatchedEntity me, Map<String, ChangeValue> changes) throws SQLException {
        if (getQuery().getUpdatePattern() != null)
            return new Change(changes, getQuery().getUpdatePattern());
        else
            return null;
    }

    @Override
    public ChangeCommand buildRemoveChange(Entity e) throws SQLException {
        if (getQuery().getRemovePattern() != null)
            return new Change(e, getQuery().getRemovePattern());
        else
            return null;
    }

    class Change implements ChangeCommand {

        private String command;

        Change(Map<String, ChangeValue> changes, String replace) {
            String input = "";
            String sp = "";
            int i = 1;
            for (Field field : getQuery().getFields()) {
                input += changes.get(field.getName()) + getQuery().getDelimiter();
                sp += "(.+)" + getQuery().getDelimiter();
            }
            sp = sp.substring(0, sp.length()-1);
            input = input.substring(0, input.length()-1);

            Pattern p = Pattern.compile(sp);
            Matcher m = p.matcher(input);
            if (m.find())
                command = m.replaceAll(replace);
        }

        Change(Entity e, String replace) {
            String input = "";
            String sp = "";
            int i = 1;
            for (Field field : getQuery().getFields()) {
                input += e.get(field.getName()) + getQuery().getDelimiter();
                sp += "(.+)" + getQuery().getDelimiter();
            }
            sp = sp.substring(0, sp.length()-1);
            input = input.substring(0, input.length()-1);

            Pattern p = Pattern.compile(sp);
            Matcher m = p.matcher(input);
            if (m.find())
                command = m.replaceAll(replace);
        }

        @Override
        public void invoke() throws Exception {
            File f = new File(getQuery().getConnection().getPath()+"/"+getQuery().getFilename());
            PrintWriter pw = new PrintWriter(new FileOutputStream(f, getQuery().getAppend()));
            pw.write(command+"\n");
            pw.close();
        }

        @Override
        public String toString() {
            return command;
        }
    }


}
