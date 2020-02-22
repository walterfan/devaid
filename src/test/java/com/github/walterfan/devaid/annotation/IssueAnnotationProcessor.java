package com.github.walterfan.devaid.annotation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.github.walterfan.devaid.util.annotation.Issue")

public class IssueAnnotationProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		Messager messager = processingEnv.getMessager();
		HashMap<String, String> map = new HashMap<String, String>();
		 for (TypeElement te : annotations) {
		      for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
		    	  messager.printMessage(Diagnostic.Kind.NOTE,
                          "Printing: " + e.toString());
		      }
		 }
		return false;
	}
	
	@Override
	  public SourceVersion getSupportedSourceVersion() {
	    return SourceVersion.latestSupported();
	  }

	public static void main(String[] args) throws IOException {

	}
}
