package com.dynamicInterpreter;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import com.dynamicInterpreter.entity.Command;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DynamicInterpreter1ApplicationTests {

	@Autowired
	  private MockMvc mockMvc;
	@Autowired
	  private MockMvc mockMvc1;
	  @Autowired
	  private ObjectMapper objectMapper;
	
	@Test
	public void contextLoads() {
		Command cmd = new Command();
		cmd.setCode("%javascript var a =1;");
		try {
			mockMvc.perform(post("/command", 42L)
			        .contentType("application/json")
			        .content(objectMapper.writeValueAsString(cmd)))
			        .andExpect(status().isOk())
			        .andExpect(jsonPath("$.res", is("")));
			cmd.setCode("%javascript print(a+1);");
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
	
	
}
