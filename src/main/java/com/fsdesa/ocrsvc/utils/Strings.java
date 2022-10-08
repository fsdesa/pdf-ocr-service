package com.fsdesa.ocrsvc.utils;

import java.util.Arrays;
import java.util.List;

public class Strings {
    public static final String concat(String... values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            builder.append(value);
        }
        return builder.toString();
    }

    public static final List<String> splitLines(String text) {
        String[] lines = text.split("\\R");
        return Arrays.asList(lines);
    }
}