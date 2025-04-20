package io.github.Spyfall.client;



public class Config {
    private static String webSocketUri;


    public static void setWebSocketUri(String uri) {
        webSocketUri = uri;
    }

    public static String getWebSocketUri() {
        return webSocketUri;
    }
}
