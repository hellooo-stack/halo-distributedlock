package site.hellooo.distributedlock.examples;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigReader {

    private static final String CONFIG_FILE = "config.json";
    private static final Gson GSON = new GsonBuilder()
            .create();

    private static JsonObject getConfig() {
        InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE);

        if (inputStream == null) {
            throw new RuntimeException("Failed to load configuration file: " + CONFIG_FILE);
        }

        InputStreamReader reader = new InputStreamReader(inputStream);
        return (JsonObject) JsonParser.parseReader(reader);
    }

    public static RedisConfig redis() {
        JsonObject config = getConfig();
        JsonObject jedisConfig = config.getAsJsonObject("jedis");
        return GSON.fromJson(jedisConfig.toString(), RedisConfig.class);
    }

    public static class RedisConfig {
        private String host;
        private int port;

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }
    }
}
