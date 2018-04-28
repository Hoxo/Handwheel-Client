package com.handwheel.parser;

import com.handwheel.model.Message;

import java.util.Map;

public interface Parser {
    Map<String, Object> parse(String message);
    Message parse(Map<String, Object> map);
}
