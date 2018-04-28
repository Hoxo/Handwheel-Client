package com.handwheel.converter;

import com.handwheel.model.Message;

import java.util.Map;

public interface Converter {
    String convert(Map<String, Object> map);
    String convert(Message message);
}
