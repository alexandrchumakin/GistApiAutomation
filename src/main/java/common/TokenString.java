package common;

import org.apache.commons.codec.binary.Base64;

import java.util.Random;

public class TokenString {
    private String tokenName;
    private String token;
    private String id;

    public TokenString(){
        this.tokenName = generateString();
    }

    public String baseEncode(){
        return String.format("Basic %1$s", new String(Base64.encodeBase64(
                String.format("%1$s:%2$s", Configurations.getValueByKey("loginUser"), Configurations.getValueByKey("loginPW")).getBytes()
        )));
    }

    public String getToken(){
        return this.token;
    }

    public void setToken(String value){
        this.token = value;
    }

    public String getTokenName(){
        return this.tokenName;
    }

    public static String generateString(){
        String alphChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * alphChars.length());
            salt.append(alphChars.charAt(index));
        }
        return salt.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
