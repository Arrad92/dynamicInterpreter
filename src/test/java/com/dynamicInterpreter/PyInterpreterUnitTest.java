package com.dynamicInterpreter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.dynamicInterpreter.factory.PyInterpreter;

@RunWith(SpringRunner.class)
public class PyInterpreterUnitTest {

	@TestConfiguration
    static class PyInterpreterTestContextConfiguration {
  
        @Bean
        public PyInterpreter PyInterpreter() {
            return PyInterpreter.getSession("1");
        }  
    }
	
	@Autowired
	PyInterpreter pyInterpreter;
	
	
	/**
     * test case :  when valid command then return the correct result.
     */
    @Test
    public void whenValid_ThenReturnResult(){	
        String res = pyInterpreter.executeCmd("print 1");
        assertThat(res )
        .isEqualTo("1");
    }
    /**
     * test case :  when the statement conatins parsing error.
     */
    @Test
    public void whenParseError_ThenReturnError(){	
        String res = pyInterpreter.executeCmd("print p");
        assertThat(res )
        .contains("NameError");
    }
}
