package mash.pies.syncthing.engine.processors.connection;

public class OAuth2Credential implements Credential {
    
    private String clientId;
    private String secret;
    private String refreshToken;

    public String getClientId() { return clientId;}
    public void setClientId(String clientId) {this.clientId = clientId;}

    public String getSecret() { return secret;}
    public void setSecret(String secret) {this.secret = secret;}

    public String getRefreshToken() { return refreshToken;}
    public void setRefreshToken(String refreshToken) {this.refreshToken = refreshToken;}
}
