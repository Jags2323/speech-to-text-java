package com.example.speechtotext.controller;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileInputStream;
import java.util.List;

@Controller
public class SpeechToTextFileUploadController {
    @PostMapping("/upload-and-transcribe")
    @ResponseBody
    public String uploadAndTranscribe(@RequestParam("audioFile") MultipartFile audioFile) {
        try {
            String jsonKeyFilePath = "C:\\Users\\jagad\\OneDrive\\Desktop\\speech-to-text\\New folder\\speech-to-text\\speech-to-text-key.json";

            Credentials credentials = ServiceAccountCredentials.fromStream(new FileInputStream(jsonKeyFilePath));

            try (SpeechClient speechClient = SpeechClient.create(
                    SpeechSettings.newBuilder()
                            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                            .build())) {
                byte[] audioData = audioFile.getBytes();
                ByteString audioBytes = ByteString.copyFrom(audioData);

                RecognitionConfig config = RecognitionConfig.newBuilder()
                        .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                        .setSampleRateHertz(24000)
                        .setLanguageCode("en-US")
                        .setEnableWordTimeOffsets(true)
                        .build();

                RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

                RecognizeResponse response = speechClient.recognize(config, audio);
                List<SpeechRecognitionResult> results = response.getResultsList();

                StringBuilder transcriptionResult = new StringBuilder();

                for (SpeechRecognitionResult result : results) {
                    List<SpeechRecognitionAlternative> alternatives = result.getAlternativesList();
                    for (SpeechRecognitionAlternative alternative : alternatives) {
                        transcriptionResult.append("Transcript: ").append(alternative.getTranscript()).append("\n");
                    }
                }

                return transcriptionResult.toString();
            }
        } catch (Exception e) {
            return "An error occurred: " + e.getMessage();
        }
    }
}
