package kea.keapoint.newsapi.controller;

import kea.keapoint.newsapi.entity.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {
    private final Config config;

    @Autowired
    public ConfigController(Config config) {
        this.config = config;
    }

    @GetMapping("/config")
    public String loadLocalConfig() {
        return config.toString();
    }

}
