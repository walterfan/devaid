package com.github.walterfan.devaid.file;


import java.io.IOException;
import java.net.URI;
import java.net.URL;

public interface ImageCollector {
	int collect(URI uri) throws IOException;
}
