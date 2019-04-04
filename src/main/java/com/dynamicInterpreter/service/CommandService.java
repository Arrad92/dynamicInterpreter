package com.dynamicInterpreter.service;

import java.io.StringWriter;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dynamicInterpreter.entity.Command;
import com.dynamicInterpreter.entity.Result;
import com.dynamicInterpreter.factory.Interpreter;
import com.dynamicInterpreter.factory.InterpreterFactory;



@Service
public class CommandService {
	 @Autowired
	 InterpreterFactory interpreterFactory;
	 /**
	  * this method treat the command passed by the Command Controller
	  * @param cmd : the Command object
	  * @param sessionId : the session Id o the connected user
	  * @return
	  */
	 public Result treatCmd(Command cmd,String sessionId){
		 Result res = new Result();
		 if(!this.parseCommand(cmd)){
			 res.setRes("Syntax Error, the command must be formatted like this %<interpreter-name><whitespace><code>");
			 return res;
		 }
		 Object[] param = this.decodeParam(cmd, sessionId);
		 String cmdLine = (String) param[0];
		 Interpreter interpreter = (Interpreter) param[1];
		 if(interpreter == null){
			res.setRes("unknown interpreter"); 
		 }else{
			 res.setRes(this.executeWithTimeOut(cmdLine, interpreter, 10));
		 }
      	return res;
	 }
	 /**
	  * this method execute a command with a timeout argument
	  * @param cmdLine
	  * @param interpreter
	  * @param timeout
	  * @return String
	  */
	 public String executeWithTimeOut(String cmdLine,Interpreter interpreter,int timeout){
		 ExecutorService executor = Executors.newCachedThreadPool();
	      	Callable<Object> task = new Callable<Object>() {
	      	   public String call() {
						return interpreter.executeCmd(cmdLine);
	      	   }
	      	};
	      	Future<Object> future =  executor.submit(task);
	      	try {
	      	   String result = (String) future.get(timeout, TimeUnit.SECONDS); 
	      	   return result;
	      	} catch (TimeoutException ex) {
	      	   // handle the timeout
	      		return "Time Limit exceeded";
	      	} catch (InterruptedException e) {
	      		return "Interrupted Exception";
	      	} catch (ExecutionException e) {
	      	   // handle other exceptions
	      		return "parsing error : "+e.getMessage();
	      	} finally {
	      	   future.cancel(true);
	      	}
	 }
	 /**
	  * this method extract the needful params from the passed command
	  * @param cmd : the Command object
	  * @param sessionId : the session Id o the connected user
	  * @return Object[]
	  */
	 public Object[] decodeParam(Command cmd,String sessionId){
		 String cmdCode = cmd.getCode();
		 int lineSpaceIndex = cmdCode.indexOf(" ");
		 String interpName =cmdCode.substring(1, lineSpaceIndex).trim();
		 String cmdLine = cmdCode.substring(lineSpaceIndex).trim();
		 Interpreter interpreter = interpreterFactory.getInterpreterBySession(interpName, sessionId);
		 return new Object[]{cmdLine,interpreter};
	 }
	 /**
	  * this method verify if the command is formatted like this:
	  * %<interpreter-name><whitespace><code>
	  * @param cmd : the Command object
	  * @return boolean
	  */
	 public boolean parseCommand(Command cmd){
		 String cmdCode = cmd.getCode().trim();
		 /*
		  * case : the command code doesn't begin with the char '%'
		  */
		 if(cmdCode.charAt(0) != '%') return false;
		 /*
		  * case : no white space
		  */
		 if(cmdCode.indexOf(" ") == -1) return false;
		 /*
		  * case : no command line passed
		  */
		 if(cmdCode.substring(cmdCode.indexOf(" ")).trim().equals("")) return false;
		 return true;
	 }
	 
}
