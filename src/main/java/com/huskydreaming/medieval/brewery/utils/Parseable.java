package com.huskydreaming.medieval.brewery.utils;

import java.util.ArrayList;
import java.util.List;

public interface Parseable {

    String prefix(Object... objects);

    String parse();

    List<String> parseList();

    default String parameterize(Object... objects) {
        String string = parse();
        for (int i = 0; i < objects.length; i++) {
            String parameter = (objects[i] instanceof String stringObject) ? stringObject : String.valueOf(objects[i]);
            string = string.replace("<" + i + ">", TextUtils.capitalize(parameter.replace("_", " ")));
        }

        return TextUtils.hex(string);
    }

    default List<String> parameterizeList(Object... objects) {
        List<String> parameterList = new ArrayList<>();
        for (String string : parseList()) {
            for (int i = 0; i < objects.length; i++) {
                string = string.replace("<" + i + ">", String.valueOf(objects[i]));
            }
            parameterList.add(TextUtils.hex(string));
        }
        return parameterList;
    }
}