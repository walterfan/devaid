package com.github.walterfan.devaid.file;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;


/**
 * Created by yafan on 31/5/2018.
 */
@Slf4j
public class FileCopier implements FileHandler {

    private Path targetPath;

    public FileCopier(Path targetPath) {
        this.targetPath = targetPath;
    }

    public Path getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(Path targetPath) {
        this.targetPath = targetPath;
    }

    @Override
    public void handle(Path filePath) throws IOException {
      log.info("copy {} to {}", filePath, targetPath);

    }
}
