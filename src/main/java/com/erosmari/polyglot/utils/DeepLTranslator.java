package com.erosmari.polyglot.utils;

import com.erosmari.polyglot.Polyglot;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class DeepLTranslator {
    private static final Map<String, String> translationCache = new ConcurrentHashMap<>();

    public static String translate(String text, String targetLang) {
        return translate(text, targetLang, "auto");
    }

    public static String translate(String text, String targetLang, String sourceLang) {
        String cacheKey = text + "|" + targetLang;
        if (translationCache.containsKey(cacheKey)) {
            return translationCache.get(cacheKey);
        }

        String apiKey = Polyglot.getInstance().getConfig().getString("deepl.api_key", "");
        if (apiKey.isEmpty()) {
            LoggingUtils.logTranslated("translate.error.missing_api_key");
            return text;
        }

        try {
            URL url = URI.create("https://api-free.deepl.com/v2/translate").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "DeepL-Auth-Key " + apiKey);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            String params = "text=" + text + "&target_lang=" + targetLang.toUpperCase() + "&source_lang=" + sourceLang.toUpperCase();
            OutputStream os = conn.getOutputStream();
            os.write(params.getBytes());
            os.flush();
            os.close();

            Scanner scanner = new Scanner(conn.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            String translatedText = extractTranslatedText(response);
            translationCache.put(cacheKey, translatedText);

            return translatedText;

        } catch (Exception e) {
            LoggingUtils.logTranslated("translate.error.request_failed", e.getMessage());
            return text;
        }
    }

    private static String extractTranslatedText(String json) {
        int indexStart = json.indexOf("\"text\":\"") + 8;
        int indexEnd = json.indexOf("\"}", indexStart);
        if (indexStart < 8 || indexEnd < 0) {
            LoggingUtils.logTranslated("translate.error.invalid_response");
            return "";
        }
        return json.substring(indexStart, indexEnd);
    }
}