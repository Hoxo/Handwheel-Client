package com.handwheel.factory;

import com.handwheel.service.*;

public class HandwheelServiceFactory implements ClientServiceFactory {
    @Override
    public UserService createUserService(MessengerService messengerService) {
        return new HandwheelUserService(messengerService);
    }

    @Override
    public AuthorizationService createAuthorizationService(MessengerService messengerService, UserService userService) {
        return new HandwheelAuthorizationService(messengerService, userService);
    }
}
