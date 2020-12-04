package soen387.chatroom.model;

import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;

public interface UserManager {
    void userAuthenticate() throws NoSuchAlgorithmException, FileNotFoundException;
}
