package com.inmobi.dx.hackathon.service;

import com.inmobi.dx.hackathon.extern.OpenAIFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TextToImageGenerationService {

    @Autowired
    private OpenAIFacade openAIFacade;

    private static int n = 3;

    public List<String> generateImages(String prompt) throws Exception {
        return openAIFacade.generateImages(prompt, n);
    }
}
