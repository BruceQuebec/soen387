package soen387.chatroom.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class User {
    int userId;
    String username;
    String email;
    String password;

    public User(String username, String email, String password, boolean ifMd5) throws NoSuchAlgorithmException {
        this.username = username;
        this.email = email;
        if(ifMd5)
            this.password = User.md5gen(password);
        else
            this.password = password;
    }

    public User(int userId, String username, String email, String password, boolean ifMd5) throws NoSuchAlgorithmException {
        this.userId = userId;
        this.username = username;
        this.email = email;
        if(ifMd5)
            this.password = User.md5gen(password);
        else
            this.password = password;
    }

    public static String md5gen(String str) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.reset();
        m.update(str.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        String hashtext = bigInt.toString(16);
        // Now we need to zero pad it if you actually want the full 32 chars.
        while(hashtext.length() < 32 ){
            hashtext = "0"+hashtext;
        }
        return hashtext;
    }

    public static void serializationToJson(List<User> users, String path) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String userInJson = gson.toJson(users);
        File file = new File(path);
        if(!file.exists()){
            file.createNewFile();
        }
        FileWriter fw= new FileWriter(file.getAbsoluteFile(),true);
        //BufferedWriter writer give better performance
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(userInJson);
        //Closing BufferedWriter Stream
        bw.close();
    }

    public static List<User> deserializationFromJson(String path) throws FileNotFoundException {
        final Type USER_TYPE = new TypeToken<List<User>>() {
        }.getType();
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(path));
        List<User> data = gson.fromJson(reader, USER_TYPE);
        return data;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
