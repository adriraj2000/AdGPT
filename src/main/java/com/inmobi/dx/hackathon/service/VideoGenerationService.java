package com.inmobi.dx.hackathon.service;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoGenerationService {

    public void generateVideoFromImages(List<String> imagePathList, String outputFolder) throws IOException, InterruptedException {
        File imagesDirectory = new File("/tmp/pics/");
        System.out.println("Creating non-existent folders..");
        imagesDirectory.mkdirs();
        new File(outputFolder).mkdirs();

        for(String imagePath : imagePathList) {
            FileUtils.copyFileToDirectory(new File(imagePath), imagesDirectory);
        }
        String outputFile = outputFolder + "/output.mp4";
        FileUtils.deleteQuietly(new File(outputFile));
        String command = "ffmpeg -r 0.25 -f image2 -pattern_type glob -i \"*?jpg\" -vcodec libx264 -crf 20 -pix_fmt yuv420p " + outputFile;
        ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
        pb.directory(imagesDirectory);
        System.out.println("Generating video with images...");
        Process p = pb.start();
        p.waitFor();
        System.out.println("Video generation complete. Deleting directory " + imagesDirectory);
        FileUtils.deleteDirectory(imagesDirectory);
    }

    public void combineMultipleMp3(List<String> mp3List, String outputFolder) throws IOException, InterruptedException {
        File audioDirectory = new File("/tmp/mp3/");
        System.out.println("Creating non-existent folders..");
        audioDirectory.mkdirs();
        new File(outputFolder).mkdirs();

        for(String mp3Path : mp3List) {
            FileUtils.copyFileToDirectory(new File(mp3Path), audioDirectory);
        }

        String concatString = "concat:";
        concatString += String.join("|", mp3List);
        String outputFile = outputFolder + "/output_merged.mp3";
        FileUtils.deleteQuietly(new File(outputFile));
        System.out.println(concatString);
        String command = "ffmpeg -i \"" + concatString + "\" -acodec  copy " + outputFile;
        ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
        pb.directory(audioDirectory);
        System.out.println("Combining mp3 files...");
        Process p = pb.start();
        p.waitFor();
        System.out.println("Mp3 merge complete. Deleting directory " + audioDirectory);
        FileUtils.deleteDirectory(audioDirectory);

    }

    public void combineAudioAndVideo(String mp3Path, String mp4Path, String outputFolder, String productName) throws IOException, InterruptedException {
        System.out.println("Creating non-existent folders..");
        new File(outputFolder).mkdirs();

        String outputFilePath = outputFolder + "/" + productName + "_output_with_sound.mp4";
        int num = 0;
        File outputFile = new File(outputFilePath);
        while (new File(outputFile.getPath()).exists()) {
            num++;
            outputFile = new File(outputFolder + "/" + productName + "_output_with_sound" + (num) + ".mp4");
        }
//        FileUtils.deleteQuietly(new File(outputFilePath));
        String command = "ffmpeg -i " + mp4Path + "  -i " + mp3Path +" -c:v copy -c:a aac " + outputFile.getPath();
        System.out.println(command);
        ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
        System.out.println("Combining mp3 and mp4 files...");
        Process p = pb.start();
        p.waitFor();
        System.out.println("Mp3 + mp4 merge complete");

    }

//    public static void main(String[] args) throws IOException, InterruptedException {
//        VideoGenerationService videoGenerationService = new VideoGenerationService();
//        List<String> images = new ArrayList<>();
//        images.add("/Users/sowmithri.ravi/Pictures/hackathon/Coke001.jpg");
//        images.add("/Users/sowmithri.ravi/Pictures/hackathon/Coke002.jpg");
//        images.add("/Users/sowmithri.ravi/Pictures/hackathon/Coke003.jpg");
//
//        List<String> mp3s = new ArrayList<>();
//        mp3s.add("/Users/sowmithri.ravi/Pictures/hackathon/output.mp3");
//        mp3s.add("/Users/sowmithri.ravi/Pictures/hackathon/output1.mp3");
//        mp3s.add("/Users/sowmithri.ravi/Pictures/hackathon/output2.mp3");
//
//
//        videoGenerationService.generateVideoFromImages(images, "/tmp/output");
//        videoGenerationService.combineMultipleMp3(mp3s, "/tmp/output");
//        videoGenerationService.combineAudioAndVideo("/tmp/output/output.mp4", "/tmp/output/output_merged.mp3", "/tmp/output");
//    }
}
