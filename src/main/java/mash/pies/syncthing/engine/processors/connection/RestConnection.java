package mash.pies.syncthing.engine.processors.connection;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.oauth2.AccessToken;



//import com.google.api.services.admin.directory.DirectoryScopes;
// refresh token generator: https://bitbucket.org/jimttsync/ttsync/src/master/TTSync/GoogleSrc/uk/co/ttsync/google/TokenRetriever.java


public class RestConnection extends Connection {

    private String serviceEndpoint;
    private String authUrl;
    private String tokenUrl;
    private Set <String> scopes = new HashSet<String> ();
    private Map <String, String> requestParameters = new HashMap<String,String> (); // things to add to path... initialise?

    private OAuth2Credential credential = new OAuth2Credential();

    public String getServiceEndpoint() {return serviceEndpoint;}
    public void setServiceEndpoint(String serviceEndpoint) {this.serviceEndpoint = serviceEndpoint;}
    public String getAuthUrl() {return authUrl;}
    public void setAuthUrl(String authUrl) {this.authUrl = authUrl;}
    public String getTokenUrl() {return tokenUrl;}
    public void setTokenUrl(String tokenUrl) {this.tokenUrl = tokenUrl;}
    public OAuth2Credential getCredential() {return credential;}
    public void setCredential(OAuth2Credential credential) {this.credential = credential;}
    public Set<String> getScopes() {return scopes;}
    public Map <String, String> getRequestParameters() {return requestParameters;}
    public void setRequestParameters(Map <String, String> params) {this.requestParameters = params;}

//    public String getType() {return "REST connection";}

    public static HttpTransport transport = new NetHttpTransport();
    public static JsonFactory json = new GsonFactory();

//    private GoogleTokenResponse resp;
//    private Instant expiry;

    private AccessToken accessToken;

    public AccessToken getAccessToken() throws IOException {
        if (accessToken == null || Date.from(Instant.now()).after(accessToken.getExpirationTime())) {
            GoogleRefreshTokenRequest req = new GoogleRefreshTokenRequest (
                transport, json,
                credential.getRefreshToken(),
                credential.getClientId(), 
                credential.getSecret() 
            );
            req.setGrantType("refresh_token");
            req.setTokenServerUrl(new GenericUrl(tokenUrl));
            GoogleTokenResponse resp = req.execute();
            accessToken = new AccessToken(resp.getAccessToken(), 
                    Date.from(Instant.now().plusSeconds(resp.getExpiresInSeconds())));
        }
        return accessToken;
    }
}