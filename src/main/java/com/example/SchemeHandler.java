package com.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefPostData;
import org.cef.network.CefPostDataElement;
import org.cef.network.CefResponse;

import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SchemeHandler extends CefResourceHandlerAdapter implements SchemeHandlerInterface {
    protected byte[] data;
    protected int offset;
    protected String mimeType;

    @Override
    public String getScheme() {
        return "client";
    }

    @Override
    public String getDomain() {
        return "";
    }

    @Override
    public void getResponseHeaders(CefResponse response,
                                   IntRef response_length,
                                   StringRef redirectUrl) {
        response.setHeaderByName("Access-Control-Allow-Origin", "*", true);
        response.setMimeType(mimeType);
        response.setStatus(200);
        // Set the resulting response length
        response_length.set(data.length);
    }

    @Override
    public synchronized boolean readResponse(byte[] data_out, int bytes_to_read, IntRef bytes_read, CefCallback callback) {
        boolean has_data = false;

        if (offset < data.length) {
            // Copy the next block of data into the buffer.
            int transfer_size = Math.min(bytes_to_read, (data.length - offset));
            System.arraycopy(data, offset, data_out, 0, transfer_size);
            offset += transfer_size;

            bytes_read.set(transfer_size);
            has_data = true;
        } else {
            offset = 0;
            bytes_read.set(0);
        }

        return has_data;
    }

    protected JsonObject parseJSONPostData(CefPostData postData) {
        Vector<CefPostDataElement> elements = new Vector<>();
        postData.getElements(elements);
        int byteCount = elements.get(0).getBytesCount();
        byte[] bytes = new byte[byteCount];
        elements.get(0).getBytes(byteCount, bytes);
        String jsonString = new String(bytes, StandardCharsets.UTF_8);
        return new Gson().fromJson(jsonString, JsonObject.class);
    }

    public Map<String, String> parseQuery(String url) {
        Map<String, String> queryMap = new HashMap<>();

        // Extract query string from URL
        int questionMarkIndex = url.indexOf('?');
        String queryString = url.substring(questionMarkIndex + 1);

        // Split key-value pairs
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                queryMap.put(key, value);
            }
        }
        return queryMap;
    }

    @SneakyThrows
    public void loadContent(String filename) {
        mimeType = "text/html";
        URL resourceURL = getClass().getResource(filename);
        InputStream inputStream = resourceURL.openStream();
        data = inputStream.readAllBytes();
//        log.info(new String(data, StandardCharsets.UTF_8));
        inputStream.close();
    }

    private void loadJson(JsonObject jsonObject) {
        loadJson(jsonObject.toString());
    }

    protected void loadJson(String json) {
        mimeType = "application/json";
        data = json.getBytes();
    }
}
