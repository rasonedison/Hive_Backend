package com.hiveBackend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/Index")
public class TestMvcController {
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public String home() {
		 return "home";
	}
}
