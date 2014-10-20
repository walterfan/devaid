package com.github.walterfan.devaid;


import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;


public class BlogApplication extends Application<BlogConfiguration> {
    public static void main(String[] args) throws Exception {
        new BlogApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<BlogConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(BlogConfiguration configuration,
                    Environment environment) {
        // nothing to do yet
    }

}
