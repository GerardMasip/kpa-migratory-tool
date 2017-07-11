package com.ernest.pocs.awsmigrator.controller;

import com.ernest.pocs.awsmigrator.builder.ScafolderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by ernestortunotorra on 10/07/2017.
 */
@Controller
public class HomeController {

    @Autowired
    ScafolderBuilder scafolderBuilder;

    @Value("${project.destination.directory}")
    private String destinationDirectory;

    @RequestMapping({ "/"})
    public String home(Model model) {
        model.addAttribute("destinationDirectory", destinationDirectory);
        return "home";
    }

    @RequestMapping(value = { "/createproject"})
    @ResponseBody
    public void createProject(String destinationPath, String projectName) throws IOException {
        scafolderBuilder.create(destinationPath, projectName);
    }

}
