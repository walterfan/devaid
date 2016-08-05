package com.github.walterfan.devaid.annotation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.github.walterfan.util.annotation.Issue;

public class SourceReader {
	private List<Map<String, String>> annotationList = new LinkedList<Map<String, String>>();
	
	@Issue(problem="how to write codes by codes", 
			rootCause="too repeated work", 
			solution="parse annotation")
	public void readSource(String filePath, String tag) throws IOException {
		
		File file = new File(filePath);
		String fileContent = FileUtils.readFileToString(file);
		int pos = 0;
		do {
			pos = readAnnotation(tag, fileContent, pos);
		} while(pos >=0);
	}

	private int readAnnotation(String tag, String fileContent, int pos) {
		int pos1 = fileContent.indexOf(tag, pos);
		if(pos1<0) return pos1;
		int pos2 = fileContent.indexOf("(", pos1);
		if(pos2<0) return pos2;
		int pos3 =  fileContent.indexOf(")", pos2);
		if(pos3<0) return pos3;
		String content = fileContent.substring(pos2 +1, pos3);
		String[] atts = content.split(",");
		Map<String, String> attMap = new HashMap<String, String>();
		for(String att: atts) {
			
			String[] arrKv = att.split("=");
			if(arrKv.length == 2) {
				String key = arrKv[0].trim();
				String val = arrKv[1].trim();
				attMap.put(key, val);
			}
		}
		annotationList.add(attMap);
		System.out.println(attMap.keySet());
		System.out.println(attMap.values());
		return pos3;
	}
	
	public static void main(String[] args) throws IOException {
		String workingDir = System.getProperty("user.dir");
		System.out.println(workingDir);
		String filePath = "./src/test/java/com/github/walterfan/devaid/util/annotation/SourceReader.java";
		new SourceReader().readSource(filePath ,"@Issue");
	}
}
