package com.inmobi.dx.hackathon.extern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class OpenAIFacade {

    private static final String OPEN_AI_KEY = "YOUR_OPEN_AI_KEY";

    public List<String> generateImages(String prompt, int n) throws Exception {
        URL url = new URL("https://api.openai.com/v1/images/generations");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + OPEN_AI_KEY);
        connection.setDoOutput(true);

        String input = "{\"prompt\":\""+ prompt +"\",\"n\":" + n + ",\"size\":\"1024x1024\"}";
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(input);
        outputStream.flush();
        outputStream.close();

        List<String> images = new ArrayList<>();
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i<jsonArray.length(); i++) {
                images.add(jsonArray.getJSONObject(i).getString("url"));
            }
//            images.add(jsonObject.getJSONArray("data").getJSONObject(0).getString("url"));
        } else {
            System.out.println("Failed to call API. Response code: " + responseCode);
        }
        return images;
    }
}
