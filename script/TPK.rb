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