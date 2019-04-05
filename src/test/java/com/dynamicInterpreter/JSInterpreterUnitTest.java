package com.dynamicInterpreter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.dynamicInterpreter.service.CommandService;
import com.dynamicInterpreter.entity.Command;
import com.dynamicInterpreter.factory.JSInterpreter;

@RunWith(SpringRunner.class)
public class JSInterpreterUnitTest {

	@TestConfiguration
    static class JSInterpreterTestContextConfiguration {
  
        @Bean
        public JSInterpreter JSInterpreter() {
            return JSInterpreter.getSession("1");
        }  
    }
	@Autowired
	JSInterpreter jsInterpreter;
	
	
	/**
     * test case :  when valid command then return the correct result.
     */
    @Test
    public void whenValid_ThenReturnResult(){	
        String res = jsInterpreter.executeCmd("print(1)");
        assertThat(res )
        .isEqualTo("1");
    }
    
    /**
     * test case :  when the statement conatins parsing error.
     */
    @Test
    public void whenParseError_ThenReturnError(){	
        String res = jsInterpreter.executeCmd("print p");
        assertThat(res )
        .contains("parsing error");
    }
    
    
	
}
