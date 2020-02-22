package com.github.walterfan.devaid.file;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

public class AppLauncher {
	public static void main(String[] args) throws IOException
	{
		try(AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {

			ctx.getEnvironment().getPropertySources().addFirst(
					new ResourcePropertySource("classpath:application.properties"));
			ctx.register(AppConfig.class);
			ctx.refresh();

			ImageFileCollector collector = ctx.getBean(ImageFileCollector.class);
			collector.collect(URI.create("file:///workspace/logs"));
		}

	}
}
