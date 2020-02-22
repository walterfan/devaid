package com.github.walterfan.devaid.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ImageFileVisitor extends SimpleFileVisitor<Path>  {

    private final AtomicInteger handleCounter = new AtomicInteger(0);

    @Autowired
	private PathMatcher imageMatcher;

    @Autowired
    private FileHandler imageHandler;


    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs){
        String filePath = file.toFile().getAbsolutePath();
        //log.info("visit file: {}" , file);
        if(attrs.isRegularFile() && imageMatcher.matches(file)){
        	try {

				imageHandler.handle(file);
                handleCounter.incrementAndGet();
			} catch (IOException e) {
				log.error("open {} error: {}" , file, e);
				return FileVisitResult.TERMINATE;
			}
        } 
        return FileVisitResult.CONTINUE;
    }

}