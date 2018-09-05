package model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

public class Gist {
    private String id;
    private String fileName;

    public Gist(HashMap gistData){
        setId(gistData.get("id").toString());
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getFileName(){
        return this.fileName;
    }

    public void setFileName(String value){
        this.fileName = value;
    }

    public static Gist fromJson(String gistDataResp){
        HashMap userData = new HashMap<String, String>();
        try {
            userData = new ObjectMapper().readValue(gistDataResp, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Gist(userData);
    }
}
