package com.dynamicInterpreter;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.dynamicInterpreter.controller.CommandController;
import com.dynamicInterpreter.service.CommandService;

@RunWith(SpringRunner.class)
@WebMvcTest(CommandController.class)
public class CommandControllerTest {
	@Autowired
	private MockMvc mockMvc ;
	@MockBean
	private CommandService cmdService;
}
