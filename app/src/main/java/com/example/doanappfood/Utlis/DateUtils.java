package com.example.doanappfood.Utlis;
public class DateUtils {

    public static String formatDate(String raw) {
        if (raw == null || raw.length() < 16) {
            return raw != null ? raw : "";
        }

        try {
            String[] parts = raw.replace("T", " ").split(" ");
            String[] d = parts[0].split("-");

            return d[2] + "/" + d[1] + "/" + d[0]
                    + " " + parts[1].substring(0, 5);

        } catch (Exception e) {
            return raw;
        }
    }
}