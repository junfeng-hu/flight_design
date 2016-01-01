package com.SpringMVC.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AnotherStudentController {

	@RequestMapping(value = "/addStudent", method = RequestMethod.POST)
	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		System.out.println(arg0.getParameter("name") + "-" + arg0.getParameter("origin_password"));
		ModelAndView view = new ModelAndView("result");
		view.addObject("name", arg0.getParameter("name"));
		view.addObject("age", arg0.getParameter("age"));
		view.addObject("id", arg0.getParameter("id"));
		return view;
	}

	@RequestMapping(value = "/student", method = RequestMethod.GET)
	public ModelAndView handleRequest2(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {

		return new ModelAndView("student", "command", new Student());
	}

	@RequestMapping(value = "/index")
	public ModelAndView handle_index(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		return new ModelAndView("index");
	}

	@RequestMapping(value = "/changePassword")
	public ModelAndView handle_change_password(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		return new ModelAndView("changePassword");
	}
}
