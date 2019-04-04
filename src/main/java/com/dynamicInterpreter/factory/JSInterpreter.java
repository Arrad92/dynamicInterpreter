package com.dynamicInterpreter.factory;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.stereotype.Component;



public class JSInterpreter implements Interpreter {
	private static  JSInterpreter instance = null;
	private static Map<String,JSInterpreter> sessionInstances = new HashMap<String,JSInterpreter>();
	private ScriptEngine engine;
	private JSInterpreter(){
		ScriptEngineManager factory = new ScriptEngineManager();
		this.engine = factory.getEngineByName("JavaScript");
	}
	
	public String executeCmd(String statement) {
		
		String res="";
		ScriptContext context = engine.getContext();
        // evaluate JavaScript code from String
        StringWriter writer = new StringWriter();
        StringWriter errorWriter = new StringWriter();
        context.setWriter(writer);
        context.setErrorWriter(errorWriter);
       
        try {
			engine.eval(statement);
			res = writer.toString().trim();
			System.out.println(res);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			res = "parsing error :" + e.getMessage();
			System.out.println(res);
			
		}
        	
        	
			
		
        
		return res;
	}
    public static JSInterpreter getInstance(){
    	if(JSInterpreter.instance == null) JSInterpreter.instance = new JSInterpreter();
    	return JSInterpreter.instance;
    }
    public static JSInterpreter getSession(String sessionId){
    	if (JSInterpreter.sessionInstances.get(sessionId)==null) JSInterpreter.sessionInstances.put(sessionId, new JSInterpreter());
    	return JSInterpreter.sessionInstances.get(sessionId);
    }
}