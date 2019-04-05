package com.dynamicInterpreter;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import com.dynamicInterpreter.entity.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PyInterpreterIntegrationTests {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MockMvc mockMvc1;
	@Autowired
	private ObjectMapper objectMapper;
	  /**
	     * test case :  when passing two succesives commands with the same session id , 
	     * the second must depend on the first command result.
	     * first command {"code" : "javascript var a=1;"}
	     * second command {"code" : "javascript print(a+1)"}
	     * first command result : {"res" : ""}
	     * second command result : {"res" : "2"}
	     */
	@Test
	public void twoCommandsWithSameSession() {
		Command cmd = new Command();
		cmd.setCode("%python a = 1");
		try {
			MvcResult mvcResult =  mockMvc.perform(post("/command", 42L)
			        .contentType("application/json")
			        .content(objectMapper.writeValueAsString(cmd)))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.res", is("")))
			        .andReturn();
			cmd.setCode("%python print a+1");
			// reuse the previous session   
			MockHttpSession session = (MockHttpSession) mvcResult
			        .getRequest().getSession();
			mockMvc.perform(post("/command", 42L)
					.session(session)
			        .contentType("application/json")
			        .content(objectMapper.writeValueAsString(cmd)))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.res", is("2")));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
     * test case :  when passing two succesives commands with different session id , 
     * the second must depend on the first command result.
     * first command {"code" : "javascript var a=1;"}
     * second command {"code" : "javascript print(a+1)"}
     * first command result : {"res" : ""}
     * second command result : {"res" : "compilation error"}
     */
	@Test
	public void twoCommandsWithDifferentSession(){
		Command cmd = new Command();
		cmd.setCode("%python a =1");
		try {
			mockMvc.perform(post("/command", 42L)
			        .contentType("application/json")
			        .content(objectMapper.writeValueAsString(cmd)))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.res", is("")));
			cmd.setCode("%python print a+1");
			mockMvc.perform(post("/command", 42L)
			        .contentType("application/json")
			        .content(objectMapper.writeValueAsString(cmd)))
			        .andExpect(status().isOk())
			        .andExpect((ResultMatcher) jsonPath("$.res", contains("parsing error :")));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
     * test case : when passing an infinite loop , the result should be a time limit exceed msg
     */
    @Test
    public void commandWithTimeLimitExceeded(){
    	Command cmd = new Command();
		cmd.setCode("%python while True: ");
		try {
			mockMvc.perform(post("/command", 42L)
			        .contentType("application/json")
			        .content(objectMapper.writeValueAsString(cmd)))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.res", is("Time Limit exceeded")));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /**
     * test : when passing a command with parsing error like %javascript p 1
     */
    @Test
    public void whenCodeCannotBeParsed_thenReturnErrorMsg(){
    	Command cmd = new Command();
		cmd.setCode("%python p");
		try {
			mockMvc.perform(post("/command", 42L)
			        .contentType("application/json")
			        .content(objectMapper.writeValueAsString(cmd)))
			        .andExpect(status().isOk())
			        .andExpect((ResultMatcher) jsonPath("$.res", contains("parsing error :")));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /**
     * test case : when passing an Invalid Interpreter
     * command : {"code":"p print 1"}
     * result : {"res":"unknown interpreter"}
     */
    @Test
    public void whenInvalidInterpreter_thenReturnUknownInterpreter() {
    	Command cmd = new Command();
		cmd.setCode("%pyt p");
		try {
			mockMvc.perform(post("/command", 42L)
			        .contentType("application/json")
			        .content(objectMapper.writeValueAsString(cmd)))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.res", is("unknown interpreter")));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /**
     * test case :  when invalid command then the result is a Syntax Error.
     */
    @Test
    public void whenInvalidCommand_ThenReturnSyntaxError(){
    	Command cmd = new Command();
		cmd.setCode("python print(1);");
		try {
			mockMvc.perform(post("/command", 42L)
			        .contentType("application/json")
			        .content(objectMapper.writeValueAsString(cmd)))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.res", is("Syntax Error, the command must be formatted like this %<interpreter-name><whitespace><code>")));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
