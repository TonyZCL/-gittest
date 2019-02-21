package cn.itcast.springboot.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    //也可以使用@Value去读取application.properties中配置的配置项
    @Autowired
    private Environment environment;

    @GetMapping("/info")
    public String info(){
        return "浪里个浪" + environment.getProperty("url");
    }
}
