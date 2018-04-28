package com.handwheel.factory;

import com.handwheel.converter.Converter;
import com.handwheel.parser.Parser;
import com.handwheel.service.MessengerService;

public interface MessengerServiceFactory {
    MessengerService createMessengerService(String address, Parser parser, Converter converter);
}
