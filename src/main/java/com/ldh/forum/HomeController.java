package com.ldh.forum;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @RequestMapping("/ldh")
    @ResponseBody
    public String showHome() {
        return "start";
    }
}
