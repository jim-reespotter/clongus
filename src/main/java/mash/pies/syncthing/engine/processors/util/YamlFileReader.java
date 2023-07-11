package mash.pies.syncthing.engine.processors.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import mash.pies.syncthing.engine.processors.ProcessorBase;
import mash.pies.syncthing.engine.processors.change.valueGenerator.*;
import mash.pies.syncthing.engine.processors.connection.*;
import mash.pies.syncthing.engine.processors.filter.*;
import mash.pies.syncthing.engine.processors.matcher.AttributeValueMatcherRule;
import mash.pies.syncthing.engine.processors.matcher.RegexMatcherRule;
import mash.pies.syncthing.engine.processors.query.*;
import mash.pies.syncthing.engine.processors.task.*;

public class YamlFileReader {

    private Set<TypeDescription> typeDefs = new HashSet<TypeDescription>();
    private Yaml yaml;

    private TaskRegistry registry;

    private static class ConfigContainer extends HashMap<String, List<ProcessorBase>> {
    }

    public YamlFileReader() throws IOException {
        this(new TaskRegistry());
    }

    public YamlFileReader(TaskRegistry registry) {
        this.registry = registry;
        setYamlTypes();
    }

    private void setYamlTypes() {

        typeDefs.add(new TypeDescription(FileConnection.class, "!FileConnection"));
        typeDefs.add(new TypeDescription(LdapConnection.class, "!LdapConnection"));
        typeDefs.add(new TypeDescription(SQLConnection.class, "!SQLConnection"));
        typeDefs.add(new TypeDescription(RestConnection.class, "!RestConnection"));

        typeDefs.add(new TypeDescription(OAuth2Credential.class, "!OAuth2"));

        typeDefs.add(new TypeDescription(CsvQuery.class, "!CsvQuery"));
        typeDefs.add(new TypeDescription(LdapObjectQuery.class, "!LdapObjectQuery"));
        typeDefs.add(new TypeDescription(LdapAttributeQuery.class, "!LdapAttributeQuery"));
        typeDefs.add(new TypeDescription(SQLQuery.class, "!SQLQuery"));
        typeDefs.add(new TypeDescription(MatchQuery.class, "!MatchQuery"));
        typeDefs.add(new TypeDescription(JsonQuery.class, "!JsonQuery"));
        typeDefs.add(new TypeDescription(ReadWriteQuery.class, "!ReadWriteQuery"));
        typeDefs.add(new TypeDescription(FileReadQuery.class, "!FileReadQuery"));
        typeDefs.add(new TypeDescription(FileWriteQuery.class, "!FileWriteQuery"));

        typeDefs.add(new TypeDescription(SimpleTaskProcessor.class, "!SimpleTask"));
        typeDefs.add(new TypeDescription(ForEachTaskProcessor.class, "!ForEachTask"));

        typeDefs.add(new TypeDescription(AllowFilter.class, "!AllowFilter"));
        typeDefs.add(new TypeDescription(RegexFilter.class, "!RegexFilter"));

        typeDefs.add(new TypeDescription(AttributeValueMatcherRule.class, "!AttributeValueMatcher"));
        typeDefs.add(new TypeDescription(RegexMatcherRule.class, "!RegexMatcher"));

        typeDefs.add(new TypeDescription(RegexAttributeValueGenerator.class, "!RegexAttribute"));
        typeDefs.add(new TypeDescription(LiteralAttributeValueGenerator.class, "!LiteralAttribute"));
        typeDefs.add(new TypeDescription(CopyAttributeValueGenerator.class, "!CopyAttribute"));
        typeDefs.add(new TypeDescription(RandomAttributeValueGenerator.class, "!RandomAttribute"));
        typeDefs.add(new TypeDescription(BitwiseAttributeValueGenerator.class, "!BitwiseAttribute"));
        typeDefs.add(new TypeDescription(GroovyAttributeValueGenerator.class, "!GroovyAttribute"));   
        yaml = new Yaml(new Constructor(new TypeDescription(ConfigContainer.class), typeDefs));
    }

    public void read(InputStream is) throws Exception {

        ConfigContainer cData = yaml.load(is);
        is.close();

        for (String itemName : cData.keySet())
            for (Object cfgMap : cData.get(itemName)) {
                Map<String, ProcessorBase> t = (Map<String, ProcessorBase>) cfgMap;
                for (ProcessorBase pb : t.values()) {

                    if (pb instanceof TaskProcessor)
                        registry.addTask((TaskProcessor)pb);
                }
            }

    }

    public TaskRegistry getRegistry() {
        return registry;
    }

    public static class TaskRegistry {

        private Map<String, Connection> connections = new HashMap<String, Connection>();
        private Map<String, Query> queries = new HashMap<String, Query>();
        private Map<String, TaskProcessor> tasks = new HashMap<String, TaskProcessor>();
    
        public void addTask(TaskProcessor tp) throws Exception {
            if (tasks.containsKey(tp.getName()))
                throw new Exception("Duplicate task named " + tp.getName() + " - dies");
            else
                tasks.put(tp.getName(), tp);
        }
    
        public void addQuery(Query q) throws Exception {
            if (queries.containsKey(q.getName()))
                throw new Exception("Duplicate query named " + q.getName() + " - dies");
            else
                queries.put(q.getName(), q);
        }
    
        public void addConnection(Connection c) throws Exception {
            if (connections.containsKey(c.getName()))
                throw new Exception("Duplicate connection named " + c.getName() + " - dies");
            else
                connections.put(c.getName(), c);
        }
    
        public Map<String, TaskProcessor> getTasks() {
            return tasks;
        }
    
    }
}
