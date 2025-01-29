package com.erosmari.polyglot.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.erosmari.polyglot.config.ConfigHandler;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

public class DeepLTranslator {

    public static String translate(String text, String targetLang) {
        String apiKey = ConfigHandler.getDeepLApiKey();
        String apiUrl = ConfigHandler.getDeepLApiUrl();

        if (apiKey == null || apiKey.isEmpty()) {
            return "§c[Error] No API Key found in config.yml!";
        }

        try {
            // 🔹 Codificar el texto para la URL
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            URI uri = new URI(apiUrl + "?auth_key=" + apiKey + "&text=" + encodedText + "&target_lang=" + targetLang);
            URL url = uri.toURL();

            // 🔹 Configurar conexión HTTP segura
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "Polyglot/1.0"); // Evita bloqueos por la API

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                Bukkit.getLogger().warning("[Polyglot] DeepL API error: " + responseCode);
                return "§c[Error] Translation service unavailable (Code: " + responseCode + ")";
            }

            // 🔹 Leer la respuesta
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            return parseTranslatedText(response.toString());

        } catch (Exception e) {
            Bukkit.getLogger().severe("[Polyglot] Translation failed: " + e.getMessage());
            return "§c[Error] Translation failed!";
        }
    }

    private static String parseTranslatedText(String json) {
        try {
            if (json == null || json.trim().isEmpty()) {
                Bukkit.getLogger().warning("[Polyglot] Warning: Received an empty response from DeepL.");
                return "§c[Error] Translation service returned an empty response!";
            }

            // 🔹 Parsear JSON de forma segura usando Gson
            JsonElement jsonElement = JsonParser.parseString(json);
            if (!jsonElement.isJsonObject()) {
                Bukkit.getLogger().warning("[Polyglot] Warning: DeepL response is not a valid JSON object.");
                return "§c[Error] Invalid response format!";
            }

            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (!jsonObject.has("translations")) {
                Bukkit.getLogger().warning("[Polyglot] Warning: DeepL response missing 'translations' field.");
                return "§c[Error] Invalid response format!";
            }

            // 🔹 Obtener la traducción correcta
            JsonElement translationsArray = jsonObject.get("translations");
            if (!translationsArray.isJsonArray() || translationsArray.getAsJsonArray().isEmpty()) {
                Bukkit.getLogger().warning("[Polyglot] Warning: DeepL response contains an empty translation array.");
                return "§c[Error] No translations found!";
            }

            JsonObject translationObject = translationsArray.getAsJsonArray().get(0).getAsJsonObject();
            if (!translationObject.has("text")) {
                Bukkit.getLogger().warning("[Polyglot] Warning: DeepL response missing 'text' field.");
                return "§c[Error] Invalid response format!";
            }

            String translatedText = translationObject.get("text").getAsString().trim();
            return translatedText.isEmpty() ? "§c[Error] Empty translation result!" : translatedText;

        } catch (Exception e) {
            Bukkit.getLogger().severe("[Polyglot] JSON parsing failed: " + e.getMessage());
            return "§c[Error] JSON parsing failed!";
        }
    }
}