
package com.dynamicInterpreter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.dynamicInterpreter.entity.Command;
import com.dynamicInterpreter.entity.Result;
import com.dynamicInterpreter.factory.InterpreterFactory;
import com.dynamicInterpreter.factory.JSInterpreter;
import com.dynamicInterpreter.factory.PyInterpreter;
import com.dynamicInterpreter.service.CommandService;


@RunWith(SpringRunner.class)
public class CommandServiceUnitTest {
	@TestConfiguration
    static class CommandServiceTestContextConfiguration {
  
        @Bean
        public CommandService CommandService() {
            return new CommandService();
        }
    }
 
    @Autowired
    private CommandService cmdService;
 
    @MockBean
    private InterpreterFactory interpreterFactory;
    
    @Before
    public void setUp() {
    	JSInterpreter jsInterpreter ;
    
    	Mockito.when(interpreterFactory.getInterpreterBySession("python", "1"))
    			.thenReturn(PyInterpreter.getSession("1"));
    	Mockito.when(interpreterFactory.getInterpreterBySession("javascript", "1"))
		.thenReturn(JSInterpreter.getSession("1"));
    }
    /**
     * test case :  when invalid command then the result is a Syntax Error.
     */
    @Test
    public void whenInvalidCommand_ThenReturnSyntaxError(){
    	Command cmd = new Command();
    	/*
    	 * no '%' special char at the begin of the command
    	 */
    	cmd.setCode("python print 1");
        Result res = cmdService.treatCmd(cmd,"1");
        assertThat(res.getRes())
        .isEqualTo("Syntax Error, the command must be formatted like this %<interpreter-name><whitespace><code>");
        /*
         * no whitspace case
         */
        cmd.setCode("%javascriptprint(1)");
        Result res1 = cmdService.treatCmd(cmd,"1");
        assertThat(res1.getRes())
        .isEqualTo("Syntax Error, the command must be formatted like this %<interpreter-name><whitespace><code>");
        /*
         * no command line
         */
        cmd.setCode("%python");
        Result res2 = cmdService.treatCmd(cmd,"1");
        assertThat(res2.getRes())
        .isEqualTo("Syntax Error, the command must be formatted like this %<interpreter-name><whitespace><code>");
    }
    /**
     * test case : when passing an Invalid Interpreter
     * command : {"code":"p print 1"}
     * result : {"res":"unknown interpreter"}
     */
    @Test
    public void whenInvalidInterpreter_thenReturnUknownInterpreter() {
    	Command cmd = new Command();
    	cmd.setCode("%p p");
        Result res = cmdService.treatCmd(cmd,"1");
    
        assertThat(res.getRes())
        .isEqualTo("unknown interpreter");
    }
    /**
     * test case :  when passing two succesives commands with the same session id , 
     * the second must depend on the first command result.
     * first command {"code" : "javascript var a=1;"}
     * second command {"code" : "javascript print(a+1)"}
     * first command result : {"res" : ""}
     * second command result : {"res" : "2"}
     */
    @Test
    public void twoCommandsWithSameSession(){
    	Command cmd = new Command();
    	cmd.setCode("%python a = 1");
        Result res = cmdService.treatCmd(cmd,"1");
        
        cmd.setCode("%python print a+1");
        res = cmdService.treatCmd(cmd,"1");
        
        assertThat(res.getRes())
        .isEqualTo("2");
        
    }
    /**
     * test case :  when passing two succesives commands with the same session id , 
     * the second must depend on the first command result.
     * first command {"code" : "javascript var a=1;"}
     * second command {"code" : "javascript print(a+1)"}
     * first command result : {"res" : ""}
     * second command result : {"res" : "compilation error"}
     */
    @Test
    public void twoCommandsWithDifferentSession(){
    	Command cmd = new Command();
    	cmd.setCode("%python a = 1");
        Result res = cmdService.treatCmd(cmd,"1");
        
        cmd.setCode("%python print a+1");
        res = cmdService.treatCmd(cmd,"2");
        
        assertThat(res.getRes())
        .isNotEqualTo("2");
        
    }
    /**
     * test case : when passing an infinite loop , the result should be a time limit exceed msg
     */
    @Test
    public void commandWithTimeLimitExceeded(){
    	Command cmd = new Command();
    	cmd.setCode("%javascript for(var i=0;i>=0;i++){print(i);}");
        Result res = cmdService.treatCmd(cmd,"1");
        assertThat(res.getRes())
        .isEqualTo("Time Limit exceeded");
    }
    /**
     * test : when passing a command with parsing error like %javascript p 1
     */
    @Test
    public void whenCodeCannotBeParsed_thenReturnErrorMsg(){
    	Command cmd = new Command();
    	/*
    	 * javascript case
    	 */
    	cmd.setCode("%javascript p");
        Result res = cmdService.treatCmd(cmd,"1");
        assertThat(res.getRes()).
        contains("parsing error :");
        /*
         * python case
         */
        cmd.setCode("%python p");
        Result res2 = cmdService.treatCmd(cmd,"1");
        assertThat(res.getRes()).
        contains("parsing error :");
    }
}