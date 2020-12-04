package soen387.chatroom.model;

import soen387.chatroom.service.userservice.UserManagerImpl;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class LocalUserManagerFactory implements UserManagerFactory {
    private String className;

    public LocalUserManagerFactory(String className){
        this.className = className;
    }

    @Override
    public UserManagerImpl getUserManager() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, MalformedURLException {
        Class< ? extends UserManager> userManagerClass = (Class<? extends UserManager>) Class.forName(this.className);
        Method getInstanceMethod = userManagerClass.getDeclaredMethod("getInstance");
        UserManagerImpl userManager = (UserManagerImpl) getInstanceMethod.invoke(userManagerClass);
        return userManager;
    }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
