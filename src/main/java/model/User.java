package model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

public class User {
    private String email;

    public User(HashMap userData){
        setEmail(userData.get("email").toString());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static User fromJson(String userObject){
        HashMap userData = new HashMap<String, String>();
        try {
            userData = new ObjectMapper().readValue(userObject, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new User(userData);
    }
}
