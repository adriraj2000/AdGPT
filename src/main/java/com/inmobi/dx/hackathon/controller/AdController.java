package com.inmobi.dx.hackathon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmobi.dx.hackathon.extern.GPT3Facade;
import com.inmobi.dx.hackathon.model.Content;
import com.inmobi.dx.hackathon.service.ContentGenerationService;
import com.inmobi.dx.hackathon.service.TextToImageGenerationService;
import com.inmobi.dx.hackathon.service.TextToVoiceGenerationService;
import com.inmobi.dx.hackathon.service.VideoGenerationService;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/api/ad")
public class AdController {

    @Autowired
    private ContentGenerationService contentGenerationService;

    @Autowired
    private TextToImageGenerationService textToImageGenerationService;

    @Autowired
    private VideoGenerationService videoGenerationService;

    @Autowired
    private TextToVoiceGenerationService textToVoiceGenerationService;

    private ObjectMapper objectMapper = new ObjectMapper();


    @GetMapping("/generate/{productName}")
    public List<Content> getContent(@PathVariable String productName) throws Exception {
        // generate image descriptions and voice over content
        String generatedContent = contentGenerationService.generateContent(productName);
        List<Content> contentList = getContentPOJOFromString(generatedContent);
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(contentList));

        int seqNo = 0;
        List<String> imgSelected = new ArrayList<>();
        List<String> mp3s = new ArrayList<>();
        FileUtils.deleteQuietly(new File("/tmp/all-pics"));
        FileUtils.deleteQuietly(new File("/tmp/audio"));
        new File("/tmp/audio").mkdirs();
//        FileUtils.deleteQuietly(new File("/tmp/output"));
        for (Content content : contentList) {
            // generate images from image descriptions
            List<String> images = textToImageGenerationService.generateImages(content.getImageDescription());

            // download images
            downloadImages(images, seqNo, "/tmp/all-pics");

            // select a random image for video
            if (images.size() > 0) {
                int randomNum = 0 + (int)(Math.random() * images.size());
                content.setImageUrl(images.get(randomNum));
                imgSelected.add("/tmp/all-pics/img" + randomNum + "_seq00" + seqNo + ".jpg");
            }

            // generate text to voice mp3
            String mp3Path = textToVoiceGenerationService.generateVoiceFromText(content.getVoiceOver(),
                    "/tmp/audio/audio_seq" + seqNo + ".mp3");
            mp3s.add(mp3Path);

            seqNo++;
        }

        // generate a video from images
        videoGenerationService.generateVideoFromImages(imgSelected, "/tmp/output");

        // merge audios
        videoGenerationService.combineMultipleMp3(mp3s, "/tmp/output");

        // combine audio and video
        videoGenerationService.combineAudioAndVideo("/tmp/output/output.mp4", "/tmp/output/output_merged.mp3", "/tmp/output",
                productName);

        System.out.println("------------------------");
        System.out.println();
        System.out.println();
        return contentList;
    }

    private void downloadImages(List<String> imageUrls, int seqNo, String path) {
        int i = 0;
        for (String image : imageUrls) {
            try {
                ProcessBuilder pb = new ProcessBuilder("wget", "-O", "img" + i + "_seq00" + seqNo + ".jpg", image);
                new File(path).mkdirs();
                pb.directory(new File(path));
                Process p = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                int exitCode = p.waitFor();
                System.out.println("Downloaded img" + i + "_seq00" + seqNo + ".jpg");
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }
    }

    private List<Content> getContentPOJOFromString(String generatedContent) throws Exception {
        JSONArray jsonArray = new JSONArray(generatedContent);
        List<Content> contentList = new ArrayList<>();
        for (int i = 0; i< jsonArray.length(); i++) {
            Content content = new Content();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            for (String key: jsonObject.keySet()) {
                if (key.contains("image") || key.contains("Image")
                        || key.contains("description") || key.contains("Description")) {
                    content.setImageDescription(jsonObject.getString(key));
                } else if (key.contains("voice") || key.contains("Voice")) {
                    content.setVoiceOver(jsonObject.getString(key));
                } else {
                    throw new Exception("Unknown key: " + key);
                }
            }
            contentList.add(content);
        }
        return contentList;
    }

}
