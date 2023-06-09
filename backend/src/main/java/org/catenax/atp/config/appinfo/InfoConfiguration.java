package org.catenax.atp.config.appinfo;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record InfoConfiguration(String name,
                                String description,
                                String version) {
}
