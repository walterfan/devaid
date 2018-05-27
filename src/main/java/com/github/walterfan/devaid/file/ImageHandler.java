package com.github.walterfan.devaid.file;


import java.io.IOException;
import java.nio.file.Path;

public interface ImageHandler {
	void handle(Path filePath) throws IOException;
}
