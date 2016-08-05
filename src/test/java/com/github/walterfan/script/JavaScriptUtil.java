package com.github.walterfan.script;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import javax.script.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.IRFactory;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.*;


public class JavaScriptUtil {
	private static Log logger = LogFactory.getLog(JavaScriptUtil.class);
	
	public static String loadScript(String fileName) {
		File file = new File(fileName);
		try {
			return FileUtils.readFileToString(file);
		} catch (IOException e) {
			return "";
		}
	}

	public static void printNode (AstNode node)
	{
		String nodeStr = "";
		int type = node.getType();
		switch (type)
		{
		case Token.NAME:
			if (node instanceof Name)
			{
				Name name = (Name)node;
				nodeStr =  name.getIdentifier();
			}
			break;
		case Token.STRING:
			if (node instanceof StringLiteral)
				nodeStr = ((StringLiteral)node).getValue();
			break;
		case Token.WHILE:
			nodeStr = "while";
			break;
		case Token.TRUE:
			nodeStr = "true";
			break;
		case Token.FALSE:
			nodeStr = "false";
			break;
		case Token.FUNCTION:
			nodeStr = "function";
			break;
		default:
			//find constant from Token.java and handle the case if you want.
			System.out.println(String.valueOf(type)); 
			return;
		}
		if (nodeStr.length() > 0)
			System.out.println(nodeStr);
	}
	
	public static AstRoot parse(String src, int startLineNum)	throws IOException {

		CompilerEnvirons env = new CompilerEnvirons();
		env.setRecoverFromErrors(true);
		env.setGenerateDebugInfo(true);
		env.setRecordingComments(true);

		StringReader strReader = new StringReader(src);

		IRFactory factory = new IRFactory(env);
		return factory.parse(strReader, null, startLineNum);
	}
	
	public static void listScriptEngines() {
		ScriptEngineManager mgr = new ScriptEngineManager();
		List<ScriptEngineFactory> factories = mgr.getEngineFactories();
		int num = 0;
		for (ScriptEngineFactory factory : factories) {

			System.out.println("ScriptEngineFactory Info");

			String engName = factory.getEngineName();
			String engVersion = factory.getEngineVersion();
			String langName = factory.getLanguageName();
			String langVersion = factory.getLanguageVersion();

			System.out.printf((++num) + ". Script Engine: %s (%s)%n", engName, engVersion);

			List<String> engNames = factory.getNames();
			for (String name : engNames) {
				System.out.printf("\tEngine Alias: %s%n", name);
			}

			System.out.printf("\tLanguage: %s (%s)%n", langName, langVersion);

		}

}
	
	public static void main(String[] args) throws Exception {
		//listScriptEngines();
		
		String strScript = loadScriptFromClassPath("date_parser.js");
        executeScript(strScript);
    }

	public static String loadScriptFromClassPath(String tplFileName) {
		InputStream is = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			is = loader.getResourceAsStream(tplFileName);
			return IOUtils.toString(is);
		} catch (Exception e) {
			logger.error("load tempate error " + tplFileName, e);
			return "";
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	public static void executeScript(String strScript) throws ScriptException {
		// create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        
        // evaluate JavaScript code from String
        engine.eval(strScript);
	}

}
