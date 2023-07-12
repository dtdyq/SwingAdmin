package org.core;

import java.io.IOException;
import java.util.Properties;

public class ConfigMgr {
    Properties properties = new Properties();

    private ConfigMgr() {
        refresh();
    }

    private void refresh() {
        Properties temp = new Properties();
        try {
            temp.load(this.getClass().getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.properties = temp;
    }

    public String getOr(String key, String def) {
        return properties.getProperty(key, def);
    }

    public boolean getBoolOr(String key, boolean def) {
        if (properties.containsKey(key)) {
            return Boolean.parseBoolean(properties.getProperty(key));
        }
        return def;
    }

    public static ConfigMgr getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static final class LazyHolder {
        public static final ConfigMgr INSTANCE = new ConfigMgr();
    }
}
