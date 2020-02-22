package com.github.walterfan.devaid.file;

import java.nio.file.Path;
import java.nio.file.PathMatcher;

public class ImageMatcher implements PathMatcher {

	@Override
	public boolean matches(Path file) {
		String filePath = file.toFile().getAbsolutePath();    
		return filePath.matches(".*\\.(jpg|jpeg|gif|bmp|png)$");
	}

}