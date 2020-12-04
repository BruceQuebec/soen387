package soen387.chatroom.model;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

public interface UserManagerFactory {
    UserManager getUserManager() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, MalformedURLException;
}
