package cn.edu.fudan.flightweb.controller;

/**
 * Created by junfeng on 7/30/15.
 */

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * site/: using resources/static/tempfile.html
 */
@Controller
@RequestMapping("/")
public class HomeController {
   @RequestMapping(method = RequestMethod.GET)
    String index(Model model) {
       model.addAttribute("euser","wpf");
       return "index.html";
   }
}
