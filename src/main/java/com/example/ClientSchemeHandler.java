package com.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cef.callback.CefCallback;
import org.cef.network.CefPostData;
import org.cef.network.CefRequest;

import java.net.URL;

@Slf4j
public class ClientSchemeHandler extends SchemeHandler {
    public final String SCHEME = "https";
    public final String DOMAIN = "purecef.me";

    @Override
    public String getScheme() {
        return SCHEME;
    }

    @Override
    public String getDomain() {
        return DOMAIN;
    }

    @SneakyThrows
    @Override
    public synchronized boolean processRequest(CefRequest request, CefCallback callback) {
        log.info("Processing " + request.getURL());
        URL url = new URL(request.getURL());
        String urlPath = url.getPath();
        String query = url.getQuery();
        if (urlPath.contains("/api")) {
            if (processApiRequest(urlPath, query, request.getMethod(), request.getPostData())) {
                callback.Continue();
                return true;
            }
            return false;
        } else {
            // Load from filesystem
            log.info("Loading " + urlPath);
            loadContent(urlPath);
            callback.Continue();
            return true;
        }
    }

    private boolean processApiRequest(String urlPath, String query, String method, CefPostData postData) {
        Gson gson = new Gson();
        mimeType = "application/json";
        switch (urlPath) {
            case "/api/data":
                switch (method.toLowerCase()) {
                    case "get":
                        String[] partyhats = {"Red partyhat", "Yellow partyhat", "Blue partyhat", "Purple partyhat"};
                        String json = gson.toJson(partyhats, String[].class);
                        loadJson(json);
                        break;
                    case "post":
                        JsonObject postObject = parseJSONPostData(postData);
                        log.info(postObject.toString());
                        loadSuccessMessage();
                }
                break;
            case "/api/more_data":
                loadJson("[\"More Data\"]");
                break;
            default:
                return false;
        }
        return true;
    }

    private void loadSuccessMessage() {
        mimeType = "application/json";
        data = "{\"message\":\"success\"}".getBytes();
    }
}
