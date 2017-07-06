package cn.edu.sjtu.se.walknshot.apiclient;

import cn.edu.sjtu.se.walknshot.apimessages.Token;
import cn.edu.sjtu.se.walknshot.apimessages.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public class ClientImpl implements Client {
    private Token token;
    private String baseUrl = "http://localhost:8080";

    public void setToken(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void register(Callback callback, String username, String password) {
        if (!Util.validUsername(username) || !Util.validPassword(password)) {
            callback.onFailure(1);
            return;
        }
        callback.onSuccess(42);
    }

    @Override
    public void login(Callback callback, String username, String password) {
        if (!Util.validUsername(username) || !Util.validPassword(password)) {
            callback.onFailure(1);
            return;
        }

        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/login")
                .post(body)
                .build();

        new OkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onNetworkFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Token token = new ObjectMapper().readValue(response.body().string(), Token.class);
                if (token.getUserId() == 0) {
                    callback.onFailure(1);
                    return;
                }
                setToken(token);
                callback.onSuccess(token.getUserId());
            }
        });
    }

    @Override
    public void isLoginValid(Callback callback) {
        callback.onSuccess(true);
    }

    @Override
    public void logout(Callback callback) {
        callback.onSuccess(true);
    }

    @Override
    public boolean isLoggedIn() {
        return getToken() != null;
    }

    @Override
    public Integer getUserId() {
        return 0;
    }
}