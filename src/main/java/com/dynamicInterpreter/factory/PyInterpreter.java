package com.dynamicInterpreter.factory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.naming.TimeLimitExceededException;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.catalina.util.SystemPropertyReplacerListener;
import org.python.core.PyInteger;
import org.python.util.PythonInterpreter;

public class PyInterpreter implements Interpreter {
	private static  PyInterpreter instance = null;
	private static Map<String,PyInterpreter> sessionInstances = new HashMap<String,PyInterpreter>();
	private ScriptEngine engine;
	private PyInterpreter(){
		ScriptEngineManager factory = new ScriptEngineManager();
		this.engine = factory.getEngineByName("python");
	}
	@Override
	public String executeCmd(String statement)  {
		String res="";
		ScriptContext context = engine.getContext();
        // evaluate JavaScript code from String
        StringWriter writer = new StringWriter();
        StringWriter errorWriter = new StringWriter();
        context.setWriter(writer);
        context.setErrorWriter(errorWriter);
        try {
        	engine.eval(statement.trim());
			res = writer.toString().replace("\n", "");
		} catch (ScriptException e) {
			res = e.getMessage();
		}catch (Exception e){
			res = e.getMessage();
		}
        
		return res;
	}
	public static PyInterpreter getSession(String sessionId){
    	if (PyInterpreter.sessionInstances.get(sessionId)==null) PyInterpreter.sessionInstances.put(sessionId, new PyInterpreter());
    	return PyInterpreter.sessionInstances.get(sessionId);
    }
}
