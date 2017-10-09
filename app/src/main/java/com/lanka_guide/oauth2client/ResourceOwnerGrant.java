package com.lanka_guide.oauth2client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class ResourceOwnerGrant extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.res_owner_layout);
    }


    public void onResOwnerButton(View view) throws IOException, JSONException {

        EditText usernameEditText = (EditText) findViewById(R.id.usernameInput);
        String username = usernameEditText.getText().toString();

        EditText passwordEditText = (EditText) findViewById(R.id.passwordInput);
        String password = passwordEditText.getText().toString();

        TextView tokenResponseText = (TextView) findViewById(R.id.tokenResponseText);
        TextView helloResponseText = (TextView) findViewById(R.id.helloResponseText);

        AccessTokenResponse accessTokenResponse = getAccessToken(username, password);
        tokenResponseText.setText(accessTokenResponse.toString());

        HelloRespose helloRespose = accessHelloApi(accessTokenResponse.getAccessToken());
        helloResponseText.setText(helloRespose.toString());
    }

    private AccessTokenResponse getAccessToken(String username, String password) throws IOException, JSONException {
        URL url = new URL("http://10.0.2.2:8080/oauth/token");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "Basic  YWNtZTphY21lc2VjcmV0");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


        List<AbstractMap.SimpleEntry<String, String>> params = new ArrayList<>();
        params.add(new AbstractMap.SimpleEntry("grant_type", "password"));
        params.add(new AbstractMap.SimpleEntry("username", username));
        params.add(new AbstractMap.SimpleEntry("password", password));

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(getQuery(params));
        writer.flush();
        writer.close();
        os.close();
        conn.connect();

        InputStream inputStream = conn.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
            // Stream was empty.  No point in parsing.
        }

        JSONObject tokenResponseJSON = new JSONObject(buffer.toString());
        String accessToken = tokenResponseJSON.getString("access_token");
        String tokenType = tokenResponseJSON.getString("token_type");
        String expiresIn = tokenResponseJSON.getString("expires_in");
        String scope = tokenResponseJSON.getString("scope");

        return new AccessTokenResponse(accessToken, tokenType, expiresIn, scope);
    }

    private HelloRespose accessHelloApi(String token) throws IOException, JSONException {
        URL url = new URL("http://10.0.2.2:8080/helloOAuth");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "bearer  " + token);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.connect();

        InputStream inputStream = conn.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
            // Stream was empty.  No point in parsing.
        }

        JSONObject helloResponseJSON = new JSONObject(buffer.toString());
        String id = helloResponseJSON.getString("id");
        String content = helloResponseJSON.getString("content");
        return new HelloRespose(id, content);
    }

    private String getQuery(List<AbstractMap.SimpleEntry<String, String>> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (AbstractMap.SimpleEntry<String, String> pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private class HelloRespose {
        String id;
        String content;

        HelloRespose(String id, String content) {
            this.id = id;
            this.content = content;
        }

        public String getId() {
            return id;
        }

        public String getContent() {
            return content;
        }

        @Override
        public String toString() {
            return "HelloRespose{" +
                    "id='" + id + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    private class AccessTokenResponse {
        String accessToken;
        String tokenType;
        String expiresIn;
        String scope;

        AccessTokenResponse(String accessToken, String tokenType, String expiresIn, String scope) {
            this.accessToken = accessToken;
            this.tokenType = tokenType;
            this.expiresIn = expiresIn;
            this.scope = scope;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public String getExpiresIn() {
            return expiresIn;
        }

        public String getScope() {
            return scope;
        }

        @Override
        public String toString() {
            return "AccessTokenResponse{" +
                    "accessToken='" + accessToken + '\'' +
                    ", tokenType='" + tokenType + '\'' +
                    ", expiresIn='" + expiresIn + '\'' +
                    ", scope='" + scope + '\'' +
                    '}';
        }
    }
}
