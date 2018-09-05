package http;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import common.Configurations;
import common.TokenString;
import model.Gist;
import model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GistRequest {
    private final String HOST = "https://api.github.com/";
    private final String GISTS_URL = HOST + "gists";

    private String token;

    public TokenString auth(){
        HttpResponse<JsonNode> jsonResponse = null;
        TokenString tokenObject = new TokenString();
        try {
            String AUTH_URL = HOST + "authorizations";
            jsonResponse = Unirest.post(AUTH_URL)
                    .header("accept", "application/json")
                    .header("Authorization", tokenObject.baseEncode())
                    .body(String.format("{\"scopes\": [\"repo\", \"user\", \"gist\"], \"note\": \"%1$s\"}", tokenObject.getTokenName()))
                    .asJson();
        } catch (UnirestException e) {
            System.out.println("Cannot authorize with provided credentials in config.");
            e.printStackTrace();
        }
        JSONObject resp = ((JSONObject) jsonResponse.getBody().getArray().get(0));
        tokenObject.setToken(resp.getString("token"));
        tokenObject.setId(resp.get("id").toString());
        this.token = tokenObject.getToken();
        return tokenObject;
    }

    public void removeToken(TokenString tokenObject){
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.delete(String.format("%1$s%2$s/%3$s", HOST, "authorizations", tokenObject.getId()))
                    .header("accept", "application/json")
                    .header("Authorization", tokenObject.baseEncode())
                    .asJson();
        } catch (UnirestException e) {
            System.out.println(String.format("Cannot remove token with id %1$s", tokenObject.getToken()));
            e.printStackTrace();
        }
        assert jsonResponse != null && jsonResponse.getStatus() == 204;
    }

    public List<String> getAllGistIds(){
        JSONArray gistsResponse = getRequest(GISTS_URL).getBody().getArray();
        List<String> gistList = new ArrayList<>();

        for (int i = 0; i < gistsResponse.length(); i++) {
            gistList.add(Gist.fromJson(gistsResponse.get(i).toString()).getId());
        }

        return gistList;
    }

    public List<String> getUsersGistIds(){
        String USERS_URL = HOST + "users";
        JSONArray gistsResponse = getRequest(String.format("%1$s/%2$s/gists", USERS_URL, Configurations.getValueByKey("loginUser")))
                .getBody().getArray();
        List<String> gistList = new ArrayList<>();

        for (int i = 0; i < gistsResponse.length(); i++) {
            gistList.add(Gist.fromJson(gistsResponse.get(i).toString()).getId());
        }

        return gistList;
    }

    public List<String> getPublicGistIds(){
        String PUBLIC_GISTS_URL = GISTS_URL + "/public";
        JSONArray gistsResponse = getRequest(PUBLIC_GISTS_URL).getBody().getArray();
        List<String> gistList = new ArrayList<>();

        for (int i = 0; i < gistsResponse.length(); i++) {
            gistList.add(Gist.fromJson(gistsResponse.get(i).toString()).getId());
        }

        return gistList;
    }

    public Gist createGist(){
        return createGist(TokenString.generateString());
    }

    public Gist createGist(String fileName){
        String data = String.format("{\"description\":\"Created via API\",\"public\":\"true\",\"files\":{\"%1$s\":{\"content\":\"Demo\"}}", fileName);
        JsonNode response = postRequest(GISTS_URL, data);
        Gist result = Gist.fromJson(response.getObject().toString());
        HashMap firstFile = (HashMap) response.getObject().getJSONObject("files").toMap().entrySet().iterator().next().getValue();
        result.setFileName(firstFile.get("filename").toString());
        return result;
    }

    public int starGist(String gistId){
        return putRequest(String.format("%1$s/%2$s/star", GISTS_URL, gistId)).getStatus();
    }

    public int unstarGist(String gistId){
        return deleteRequest(String.format("%1$s/%2$s/star", GISTS_URL, gistId)).getStatus();
    }

    public int checkGistStarred(String gistId){
        return getRequest(String.format("%1$s/%2$s/star", GISTS_URL, gistId)).getStatus();
    }

    public Gist getGist(String gistId){
        JsonNode response = getRequest(String.format("%1$s/%2$s", GISTS_URL, gistId)).getBody();
        return Gist.fromJson(response.getObject().toString());
    }

    public void deleteGist(String gistId){
        deleteRequest(String.format("%1$s/%2$s", GISTS_URL, gistId));
    }

    public User getUser(){
        String USER_URL = HOST + "user";
        JsonNode response = getRequest(USER_URL).getBody();
        return User.fromJson(response.getObject().toString());
    }

    private HttpResponse<JsonNode> getRequest(String  url){
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = Unirest.get(url)
                    .header("accept", "application/json")
                    .header("Authorization", String.format("token %1$s", this.token))
                    .asJson();
        } catch (UnirestException e) {
            System.out.println("Cannot get response with url: " + url);
            e.printStackTrace();
        }
        return  jsonResponse;
    }


    private JsonNode postRequest(String url, String data){
        return requestWithBody(Unirest.post(url), data).getBody();
    }


    private HttpResponse<JsonNode> putRequest(String url){
        return requestWithBody(Unirest.put(url), "");
    }


    private HttpResponse<JsonNode> deleteRequest(String url){
        return requestWithBody(Unirest.delete(url), "");
    }

    private HttpResponse<JsonNode> requestWithBody(HttpRequestWithBody req, String data){
        HttpResponse<JsonNode> jsonResponse = null;
        try {
            jsonResponse = req
                    .header("accept", "application/json")
                    .header("Authorization", String.format("token %1$s", this.token))
                    .body(data)
                    .asJson();
        } catch (UnirestException e) {
            System.out.println("Cannot get response with url: " + req.getUrl());
            e.printStackTrace();
        }
        return jsonResponse;
    }
}
