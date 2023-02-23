package com.inmobi.dx.hackathon.service;

import com.google.cloud.texttospeech.v1beta1.*;
import com.google.protobuf.ByteString;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class TextToVoiceGenerationService {

    public String generateVoiceFromText(String text, String outputAudioFilePath) throws Exception {

        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create())
        {
            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            // Build the voice request; languageCode can be a random one from the given array
            String[] languageCodes = new String[]{"en-US","en-AU","en-IN","en-GB"};
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setLanguageCode(languageCodes[(int) (Math.random()*languageCodes.length)])
                    .setSsmlGender(SsmlVoiceGender.SSML_VOICE_GENDER_UNSPECIFIED)
                    .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();// MP3 audio

            // Perform the text-to-speech request
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            try (OutputStream out = new FileOutputStream(outputAudioFilePath)) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file " + outputAudioFilePath);
            }
        }
        return outputAudioFilePath;
    }
}




