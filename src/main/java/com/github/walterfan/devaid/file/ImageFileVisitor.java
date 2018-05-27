package com.github.walterfan.devaid.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;


public class ImageFileVisitor extends SimpleFileVisitor<Path> {
	private final ImageHandler imageHandler;
	private final PathMatcher imageMatcher;
	
    public ImageFileVisitor(ImageHandler imageHandler, PathMatcher imageMatcher){
        this.imageHandler = imageHandler;
        this.imageMatcher = imageMatcher;
    }
    
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs){
        String filePath = file.toFile().getAbsolutePath();       
        if(attrs.isRegularFile() && imageMatcher.matches(file)){
        	try {
				imageHandler.handle(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return FileVisitResult.TERMINATE;
			}
        } 
        return FileVisitResult.CONTINUE;
    }

}