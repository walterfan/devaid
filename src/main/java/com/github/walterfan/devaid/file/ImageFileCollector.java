package com.github.walterfan.devaid.file;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ImageFileCollector implements ImageCollector {

	@Autowired
	private FileVisitor<Path> imageVisitor;


	@Override
	public int collect(URI uri) throws IOException {

		Files.walkFileTree(Paths.get(uri),imageVisitor );
		return 0;
	}


}
