package mash.pies.syncthing.engine.processors.query;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
//import mash.pies.syncthing.engine.processors.query.JsonQuery.GenericJsonClient.Builder;

/**
 * 
 */
public class JsonQuery extends Query {
    
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
     * 
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

    private String getUrlTemplate() {
        String template = "?";

        for (String key : getConnection().getRequestParameters().keySet())
            template += key+"="+getConnection().getRequestParameters().get(key)+"&";
//        if (template.length() == 1)
  //          return null;
    //    else
            return template.substring(0, template.length()-1);
    }
    @Override
    protected Set<Entity> read(Map<String, String> params) throws Exception {

        debug("Reading entries from REST");
        if (client == null)
            init();

        Set<Entity> entities = new HashSet<Entity>();
        
        GenericJson json = client.list().execute();  // pagination??

    //    System.out.println (json.toPrettyString());
        JsonObject obj = JsonParser.parseString(json.toString()).getAsJsonObject();

        Iterator<JsonElement> iter = obj.getAsJsonArray(getSubElement()).iterator();
        while (iter.hasNext()) {
            JsonObject o = iter.next().getAsJsonObject();

            trace("Proxcessing item: "+o.toString());

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
            trace("imported " + e.toString());
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

    public class Builder extends AbstractGoogleJsonClient.Builder {

        protected Builder(HttpTransport transport, JsonFactory jsonFactory, String rootUrl, String servicePath,
                HttpRequestInitializer httpRequestInitializer, boolean legacyDataWrapper) {
            super(transport, jsonFactory, rootUrl, servicePath, httpRequestInitializer, legacyDataWrapper);
        }

        @Override
        public GenericJsonClient build() {
            return new GenericJsonClient(this);
        }
    }
    
    class GenericJsonClient extends AbstractGoogleJsonClient {

        public GenericJsonClient(Builder builder) {
            super(builder);
        }



        public List list () throws IOException {
            List list = new List ();
            initialize(list);
            return list;
        }

        class List extends GenericJsonRequest {

            protected List() {
                super("GET", getUrlTemplate(), null); 
            }

        }

        public Create create(GenericJson obj) throws IOException {
            Create create = new Create(obj);
            initialize(create);
            return create;
        }

        class Create extends GenericJsonRequest {

            protected Create(GenericJson obj) {//GenericJsonClient.this, "POST",
                super("POST", getUrlTemplate(), obj); //Entity.class??!?
            }
        }

        public Update update(String id, GenericJson obj) throws IOException {
            Update update = new Update(id, obj);
            initialize(update);
            return update;
        }

        class Update extends GenericJsonRequest {

            protected Update(String id, GenericJson obj) {
                super("PUT", id+getUrlTemplate(), obj);
            }
        }

        public Remove remove(GenericJson obj) throws IOException {
            Remove remove = new Remove(obj);
            initialize(remove);
            return remove;
        }

        class Remove extends GenericJsonRequest {

            protected Remove(GenericJson obj) {
                super("DELETE", obj.get("id")+getUrlTemplate(), null);
            }
        }
        
        abstract class GenericJsonRequest extends AbstractGoogleJsonClientRequest<GenericJson> {

            protected GenericJsonRequest(String requestMethod, String uriTemplate, Object jsonContent) {
                super(GenericJsonClient.this, requestMethod, uriTemplate, jsonContent, GenericJson.class);
            }
        }
    }
}
