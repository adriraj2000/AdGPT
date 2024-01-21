package com.inmobi.dx.hackathon.extern;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class GPT3Facade {


    private static final String OPEN_AI_KEY = "KEY";

    public String generateContent(String prompt) throws Exception {
//        URL url = new URL("https://api.openai.com/v1/engines/davinci/completions");
        URL url = new URL("https://api.openai.com/v1/engines/text-davinci-003/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + OPEN_AI_KEY);
        connection.setDoOutput(true);

        String input = "{\"prompt\":\""+ prompt +"\",\"max_tokens\":1024,\"temperature\":0.5,\"stop\":\"None\"}";
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(input);
        outputStream.flush();
        outputStream.close();

        String output = null;
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonObject = new JSONObject(sb.toString());
            output = jsonObject.getJSONArray("choices").getJSONObject(0).getString("text");
        } else {
            System.out.println("Failed to call API. Response code: " + responseCode);
        }
        return output;
    }

}
