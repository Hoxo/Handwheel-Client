package com.handwheel.factory;

import com.handwheel.model.User;
import com.handwheel.service.AuthorizationService;
import com.handwheel.service.MessengerService;
import com.handwheel.service.UserService;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

public class URLClientServiceFactory implements ClientServiceFactory {

    private URL url;
    private URLClassLoader urlClassLoader;
    private Class<UserService> userServiceClass;
    private Class<AuthorizationService> authorizationServiceClass;
    private String userServiceClassname, authServiceClassname;

    public URLClientServiceFactory(URL url, String userServiceClassname, String authServiceClassname) throws ClassNotFoundException {
        this.url = url;
        if (userServiceClassname == null || authServiceClassname == null)
            throw new IllegalArgumentException();
        this.authServiceClassname = authServiceClassname;
        this.userServiceClassname = userServiceClassname;
        load();
    }

    private void load() throws ClassNotFoundException {
        urlClassLoader = URLClassLoader.newInstance(new URL[] {url});
        userServiceClass = (Class<UserService>) urlClassLoader.loadClass(userServiceClassname);
        authorizationServiceClass =
                (Class<AuthorizationService>) urlClassLoader.loadClass(authServiceClassname);
    }

    @Override
    public UserService createUserService(MessengerService messengerService) {
        try {
            UserService userService = userServiceClass.getConstructor(MessengerService.class).newInstance(messengerService);
            return userService;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public AuthorizationService createAuthorizationService(MessengerService messengerService, UserService userService) {
        try {
            AuthorizationService authorizationService = authorizationServiceClass
                    .getConstructor(MessengerService.class, UserService.class)
                    .newInstance(messengerService, userService);
            return authorizationService;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
