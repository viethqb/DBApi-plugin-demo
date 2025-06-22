package com.gitee.freakchicken.demo.plugin;

public class App {
    /**
     * Test redis connection
     * @param args
     */
    public static void main(String[] args) {
        RedisCachePlugin plugin = new RedisCachePlugin();
        plugin.init();
        plugin.testCollection();
    }
}
