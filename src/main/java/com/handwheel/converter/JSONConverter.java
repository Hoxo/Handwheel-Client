package com.handwheel.converter;

import com.alibaba.fastjson.JSONObject;
import com.handwheel.model.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class JSONConverter implements Converter {
    @Override
    public String convert(Map<String, Object> map) {
        JSONObject object = new JSONObject(map);
        return object.toJSONString();
    }

    @Override
    public String convert(Message message) {
        JSONObject obj = new JSONObject();
        JSONObject temp = new JSONObject();
        temp.put("text",message.getText());
        temp.put("sender",message.getSender());
        temp.put("date", message.getDate().format(DateTimeFormatter.ISO_DATE_TIME));
        temp.put("destination", message.getDestination());
        obj.put("msg",temp);
        return obj.toJSONString();
    }
}
