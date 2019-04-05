package com.dynamicInterpreter.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dynamicInterpreter.entity.Command;
import com.dynamicInterpreter.entity.Result;
import com.dynamicInterpreter.service.CommandService;

@RestController
public class CommandController {
	@Autowired
    CommandService cmdService;
	@PostMapping("/execute")
	Result runCommand(@RequestBody Command cmd, HttpSession session) {
		return cmdService.treatCmd(cmd,session.getId());
	}
	
}
