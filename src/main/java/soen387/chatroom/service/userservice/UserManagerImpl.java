package soen387.chatroom.service.userservice;

import soen387.chatroom.model.User;
import soen387.chatroom.model.UserManager;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserManagerImpl implements UserManager {
    private Map<String, String> context;
    private List<User> userMatched;

    private UserManagerImpl(){
        this.context = new HashMap<>();
        this.userMatched = new ArrayList<>();
    };

    private static class InstanceGenerator{ private static UserManagerImpl INSTANCE = new UserManagerImpl();}

    public static UserManagerImpl getInstance(){
        return InstanceGenerator.INSTANCE;
    }
    @Override
    public void userAuthenticate() throws NoSuchAlgorithmException, FileNotFoundException {
        String passwordMd5 = User.md5gen(this.context.get("password"));
        List<User> userdata = User.deserializationFromJson(this.context.get("userFilePath"));
        this.userMatched = userdata.stream().filter(user -> user.getUsername().equals(this.context.get("username")) &&
                                                                user.getPassword().equals(passwordMd5)).collect(Collectors.toList());
    }

    public List<User> getUserMatched() { return userMatched; }
    public void setUserMatched(List<User> userMatched) { this.userMatched = userMatched; }
    public Map<String, String> getContext() { return context; }
    public void setContext(Map<String, String> context) { this.context = context; }
}
