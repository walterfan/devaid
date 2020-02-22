package com.github.walterfan.devaid.annotation;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.FileUtils;

import com.github.walterfan.util.annotation.Issue;
import org.apache.commons.lang.StringUtils;

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


		printMarkdownTable(this.annotationList);

	}


	@Issue(problem="how to print markdown table",
			rootCause="the format is not good",
			solution="parse collection")
	public void printMarkdownTable(Collection<Map<String, String>> collection)  {

		System.out.println("| problem | root cause | solution | ");
		System.out.println("|---|---|---| ");

		collection.stream().forEach(x -> {
			System.out.println(String.format("| %s | %s | %s |",
					StringUtils.strip(x.get("problem"),  "\""),
					StringUtils.strip(x.get("rootCause"),  "\""),
					StringUtils.strip(x.get("solution"),  "\"")));
		});
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

		return pos3;
	}
	
	public static void main(String[] args) throws IOException {
		String workingDir = System.getProperty("user.dir");
		System.out.println(workingDir);
		String filePath = "./src/test/java/com/github/walterfan/devaid/annotation/SourceReader.java";
		new SourceReader().readSource(filePath ,"@Issue");
	}
}
