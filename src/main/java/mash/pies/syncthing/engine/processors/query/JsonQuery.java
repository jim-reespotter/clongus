package mash.pies.syncthing.engine.processors.query;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.googleapis.services.json.CommonGoogleJsonClientRequestInitializer;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import mash.pies.syncthing.engine.processors.Entity;
import mash.pies.syncthing.engine.processors.change.ChangeCommandGenerator;
import mash.pies.syncthing.engine.processors.change.RestChangeCommandGenerator;
import mash.pies.syncthing.engine.processors.connection.RestConnection;
import mash.pies.syncthing.engine.processors.query.JsonQuery.GenericJsonClient.Builder;

/**
 * 
 */
public class JsonQuery extends Query {
    
    static Logger logger = LogManager.getLogger();

    private String subContext;
    private String subElement;

    private RestConnection connection;

    public String getSubContext() {return subContext;}
    public void setSubContext(String subContext) {this.subContext = subContext;}
    public String getSubElement() {return subElement;}
    public void setSubElement(String subElement) {this.subElement = subElement;}

    public RestConnection getConnection() {return connection;}
    public void setConnection(RestConnection connection) {this.connection = connection;}


    // move this to connection?
    private GenericJsonClient client;

    /**
     * remove try, add throws?
     * @param cfg
     */
    private void init () throws IOException {

        GoogleCredentials cred = GoogleCredentials.create(connection.getAccessToken());
    
        Builder builder = new Builder(
                RestConnection.transport, RestConnection.json,
                connection.getServiceEndpoint(),
                getSubContext(),
                new HttpCredentialsAdapter(cred), false
        );
        builder.setApplicationName("Generic JSON");
        builder.setGoogleClientRequestInitializer(new CommonGoogleJsonClientRequestInitializer.Builder().build());
        client = new GenericJsonClient(builder);
    }

    @Override
    protected Set<Entity> read(Map<String, String> params) throws Exception {

        if (client == null)
            init();

        Set<Entity> entities = new HashSet<Entity>();
        
        GenericJson json = client.list().execute();  // pagination??

        System.out.println (json.toPrettyString());
        JsonObject obj = JsonParser.parseString(json.toString()).getAsJsonObject();

        Iterator<JsonElement> iter = obj.getAsJsonArray(getSubElement()).iterator();
        while (iter.hasNext()) {
            JsonObject o = iter.next().getAsJsonObject();

            Entity e = new Entity();
            for (String key: o.keySet()) {
                JsonElement value = o.get(key);
                if (value instanceof JsonPrimitive) 
                    e.put(key, value.getAsString());
                else 
                    if (value instanceof JsonObject) 
                        e.put(key, value.getAsJsonObject());// somehow deal with collections+nested elements here too...?
                    else if (value instanceof JsonArray)
                    e.put(key, value.getAsJsonArray());
            }
            entities.add(e);
        }

        return entities;
    }

    public Entity create(GenericJson obj) throws IOException {

        if (client == null)
            init();

        Entity e = new Entity();
        GenericJson gj = client.create(obj).execute();
        e.putAll(gj);
        return e;
    }

    public Entity update(String id, GenericJson obj) throws IOException {
    
        if (client == null)
            init();

        Entity e = new Entity();
        GenericJson gj = client.update(id, obj).execute();
        e.putAll(gj);
        return e;
    }

    public void remove(GenericJson obj) throws IOException {
        if (client == null)
            init();
        GenericJson gj = client.remove(obj).execute();
    }

    @Override
    protected ChangeCommandGenerator<?> getChangeCommandGenerator(Map<String, String> params) {
        return new RestChangeCommandGenerator(this, params);
    }

    static class GenericJsonClient extends AbstractGoogleJsonClient {

        public GenericJsonClient(Builder builder) {
            super(builder);
        }

        public static class Builder extends AbstractGoogleJsonClient.Builder {

            protected Builder(HttpTransport transport, JsonFactory jsonFactory, String rootUrl, String servicePath,
                    HttpRequestInitializer httpRequestInitializer, boolean legacyDataWrapper) {
                super(transport, jsonFactory, rootUrl, servicePath, httpRequestInitializer, legacyDataWrapper);
            }
    
            @Override
            public GenericJsonClient build() {
                return new GenericJsonClient(this);
            }
        }

        public List list () throws IOException {
            List list = new List ();
            initialize(list);
            return list;
        }

        class List extends GenericJsonRequest {

            protected List() {
                super("GET", "?domain=sebet.org.uk", null); //Entity.class??!?
            }

        }

        public Create create(GenericJson obj) throws IOException {
            Create create = new Create(obj);
            initialize(create);
            return create;
        }

        class Create extends GenericJsonRequest {

            protected Create(GenericJson obj) {//GenericJsonClient.this, "POST",
                super("POST", "?domain=sebet.org.uk", obj); //Entity.class??!?
            }
        }

        public Update update(String id, GenericJson obj) throws IOException {
            Update update = new Update(id, obj);
            initialize(update);
            return update;
        }

        class Update extends GenericJsonRequest {

            protected Update(String id, GenericJson obj) {
                super("PUT", id+"?domain=sebet.org.uk", obj);
            }
        }

        public Remove remove(GenericJson obj) throws IOException {
            Remove remove = new Remove(obj);
            initialize(remove);
            return remove;
        }

        class Remove extends GenericJsonRequest {

            protected Remove(GenericJson obj) {
                super("DELETE", obj.get("email")+"?domain=sebet.org.uk", null);
            }}
        
        abstract class GenericJsonRequest extends AbstractGoogleJsonClientRequest<GenericJson> {

            protected GenericJsonRequest(String requestMethod, String uriTemplate, Object jsonContent) {
                super(GenericJsonClient.this, requestMethod, uriTemplate, jsonContent, GenericJson.class);
            }
        }
    }
}
