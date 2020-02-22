package com.github.walterfan.devaid.file;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;

@Configuration
@ComponentScan("com.github.walterfan.devaid.file")
public class AppConfig {

    @Bean
    public PathMatcher imageMatcher() {
        return new ImageMatcher();
    }


    @Bean
    public FileHandler fileHandler() {
        return  new FileCopier(Paths.get("."));
    }


}
