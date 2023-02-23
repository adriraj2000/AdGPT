package com.inmobi.dx.hackathon.service;

import com.inmobi.dx.hackathon.extern.GPT3Facade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentGenerationService {

    @Autowired
    GPT3Facade GPT3Facade;
    private static String inputTemplate = "Can you suggest a sequence of image descriptions and their voiceover contents" +
            " to be shown for a '%s' Ad as a json array?";

    public String generateContent(String inputProduct) throws Exception {
        String prompt = String.format(inputTemplate, inputProduct);
        System.out.println(prompt);
        String output = GPT3Facade.generateContent(prompt);

        return output;
    }
}
