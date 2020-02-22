package com.github.walterfan.devaid.file;


import java.io.IOException;
import java.nio.file.Path;

public interface FileHandler {
	void handle(Path filePath) throws IOException;
}
