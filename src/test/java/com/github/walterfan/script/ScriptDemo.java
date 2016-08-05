package com.github.walterfan.script;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.*;

public class ScriptDemo {
	private ScriptEngineManager scriptManager;
	private ScriptEngine scriptEngine;
	
	public ScriptDemo() {
		scriptManager = new ScriptEngineManager();
		scriptEngine = scriptManager.getEngineByName("JavaScript");
	}
	public void callFunction() throws Exception {
        

        // JavaScript code in a String
        String script = "function hello(name) { print('Hello, ' + name); }";
        // evaluate script
        scriptEngine.eval(script);

        // javax.script.Invocable is an optional interface.
        // Check whether your script engine implements or not!
        // Note that the JavaScript engine implements Invocable interface.
        Invocable inv = (Invocable) scriptEngine;

        // invoke the global function named "hello"
        inv.invokeFunction("hello", "Scripting!!" );
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	
    	JavaScriptUtil.listScriptEngines();
    	
        ScriptEngineManager manager = new ScriptEngineManager();
        File scriptsDir = new File("./script");
        File[] scripts = scriptsDir.listFiles(new TPKFilter());
        for (File f : scripts) {
            String fileName = f.getName();
            String ext = fileName.substring(fileName.lastIndexOf(".")+1);            
            ScriptEngine engine = manager.getEngineByExtension(ext);
            if (engine != null) {
                try {
                    ScriptEngineFactory factory = engine.getFactory();
                    System.out.println("Running " + fileName + " using engine " +
                        factory.getEngineName() + " Version " +
                        factory.getEngineVersion() + " for language " +
                        factory.getLanguageName());
                    engine.eval(new FileReader(f));                    
                }
                catch (FileNotFoundException ex) {
                    Logger.getLogger(ScriptDemo.class.getName()).log(Level.SEVERE, null, ex);
                }                
                catch (ScriptException ex) {
                    Logger.getLogger(ScriptDemo.class.getName()).log(Level.SEVERE, null, ex);
                }
                finally {
                    System.out.println("============================");
                }
            }
            else {
                System.err.println("Could not find scripting engine for " + f.getName());
            }
        }
    }
}
 
class TPKFilter implements FilenameFilter {
 
    public boolean accept(File dir, String name) {
        return name.startsWith("TPK.");
    }
     
}