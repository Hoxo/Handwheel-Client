package com.handwheel.factory;

import com.handwheel.service.AuthorizationService;
import com.handwheel.service.MessengerService;
import com.handwheel.service.UserService;

public interface ClientServiceFactory {
    UserService createUserService(MessengerService messengerService);
    AuthorizationService createAuthorizationService(MessengerService messengerService, UserService userService);
}
