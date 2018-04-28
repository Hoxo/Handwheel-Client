package com.handwheel.factory;

import com.handwheel.converter.Converter;
import com.handwheel.parser.Parser;
import com.handwheel.service.MessengerService;
import com.handwheel.service.WebSocketMessengerService;

public class WebSocketServiceFactory implements MessengerServiceFactory {
    @Override
    public MessengerService createMessengerService(String address, Parser parser, Converter converter) {
        return new WebSocketMessengerService(address, parser, converter);
    }
}
