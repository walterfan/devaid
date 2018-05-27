package com.github.walterfan.devaid.file;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageFileCollector implements ImageCollector {
	
	@Autowired
	private SimpleFileVisitor<Path> imageFileVisitor;

	@Override
	public int collect(URL url) {
		//Files.walkFileTree(Paths.get(url), new ImageVisitorimageFileVisitor);
		return 0;
	}


	
}
