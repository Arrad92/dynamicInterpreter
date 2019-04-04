package com.dynamicInterpreter.factory;

import org.springframework.stereotype.Component;

@Component
public class InterpreterFactory {
	
	public Interpreter getInterpreter(String interpName){
		if(interpName.equals("javascript")){
			return  JSInterpreter.getInstance();
		}
			return null;
	}
	public Interpreter getInterpreterBySession(String interpName,String sessionId){
		if(interpName.equals("javascript")){
			return  JSInterpreter.getSession(sessionId);
		}
		if(interpName.equals("python")){
			return  PyInterpreter.getSession(sessionId);
		}
			return null;
	}
	
}
