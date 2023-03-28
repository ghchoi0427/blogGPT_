package org.example.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JsonUtils {

    public static String getResponseContent(String jsonString) {
        String content = null;
        int index = jsonString.indexOf("content\":\"");
        if (index >= 0) {
            int endIndex = jsonString.indexOf("\"", index + 10);
            if (endIndex >= 0) {
                content = jsonString.substring(index + 10, endIndex);
            }
        }
        return Objects.requireNonNullElse(content, "Content not found");
    }

    public static String extractURLFromJSON(String jsonStr) {
        String url = null;
        try {
            String[] dataArray = getArrayFromJson(jsonStr, "data");
            if (dataArray.length > 0) {
                String dataObj = dataArray[0];
                url = getObjectFromJson(dataObj, "url");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String[] getArrayFromJson(String jsonString, String key) {
        List<String> values = new ArrayList<>();
        int start = jsonString.indexOf("\"" + key + "\":[");
        if (start != -1) {
            start += key.length() + 3;
            int end = jsonString.indexOf("]", start);
            String[] parts = jsonString.substring(start, end).split("\\{");
            for (String part : parts) {
                if (part.length() > 2) {
                    values.add("{" + part);
                }
            }
        }
        return values.toArray(new String[0]);
    }

    private static String getObjectFromJson(String jsonString, String key) {
        String value = null;
        int start = jsonString.indexOf("\"" + key + "\":");
        if (start != -1) {
            start += key.length() + 3;
            int end = jsonString.indexOf(",", start);
            if (end == -1) {
                end = jsonString.indexOf("}", start);
            }
            value = jsonString.substring(start, end);
            value = value.replaceAll("\"", "");
        }
        return value;
    }
}
