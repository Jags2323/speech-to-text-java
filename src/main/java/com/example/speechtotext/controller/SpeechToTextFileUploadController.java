package com.example.speechtotext.controller;
//package com.example.speechtotext.controller;
//
//import com.google.api.gax.core.FixedCredentialsProvider;
//import com.google.auth.Credentials;
//import com.google.auth.oauth2.ServiceAccountCredentials;
//import com.google.cloud.speech.v1.*;
//import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
//import com.google.cloud.speech.v1.SpeechRecognitionResult;
//import com.google.protobuf.ByteString;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.FileInputStream;
//import java.util.List;
//
//@RestController
//public class SpeechToTextWebController {
//    @GetMapping("/transcribe-audio")
//    public String transcribeAudio() {
//        // Replace this with your actual Google Cloud Storage URL
//        String audioGcsUri = "gs://cloud-samples-data/speech/brooklyn_bridge.raw";
//
//        // Replace this with the path to your service account JSON key file
//        String jsonKeyFilePath = "C:\\\\Users\\\\jagad\\\\OneDrive\\\\Desktop\\\\speech-to-text\\\\New folder\\\\speech-to-text\\\\speech-to-text-key.json";
//
//        try {
//            Credentials credentials = ServiceAccountCredentials.fromStream(new FileInputStream(jsonKeyFilePath));
//
//            try (SpeechClient speechClient = SpeechClient.create(
//                    SpeechSettings.newBuilder()
//                            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
//                            .build())) {
//                // Create the recognition request
//                RecognitionConfig config =
//                        RecognitionConfig.newBuilder()
//                                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
//                                .setSampleRateHertz(16000)
//                                .setLanguageCode("en-US")
//                                .setAudioChannelCount(2)
//                                .setEnableWordTimeOffsets(true)
//                                .build();
//
//                RecognitionAudio audio = RecognitionAudio.newBuilder()
//                        .setUri(audioGcsUri) // Use the GCS URI here
//                        .build();
//
//                // Perform the speech recognition
//                RecognizeResponse response = speechClient.recognize(config, audio);
//                List<SpeechRecognitionResult> results = response.getResultsList();
//
//                StringBuilder transcriptionResult = new StringBuilder();
//
//                for (SpeechRecognitionResult result : results) {
//                    List<SpeechRecognitionAlternative> alternatives = result.getAlternativesList();
//                    for (SpeechRecognitionAlternative alternative : alternatives) {
//                        transcriptionResult.append("Transcript: ").append(alternative.getTranscript()).append("\n");
//                    }
//                }
//
//                return transcriptionResult.toString();
//            }
//        } catch (Exception e) {
//            // Handle exceptions and return an error response if needed
//            return "An error occurred: " + e.getMessage();
//        }
//    }
//}


import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileInputStream;
import java.util.List;

@Controller
public class SpeechToTextFileUploadController {
    @PostMapping("/upload-and-transcribe")
    @ResponseBody
    public String uploadAndTranscribe(@RequestParam("audioFile") MultipartFile audioFile) {
        try {
            // Replace with the path to your service account JSON key file
            String jsonKeyFilePath = "C:\\\\\\\\Users\\\\\\\\jagad\\\\\\\\OneDrive\\\\\\\\Desktop\\\\\\\\speech-to-text\\\\\\\\New folder\\\\\\\\speech-to-text\\\\\\\\speech-to-text-key.json";

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
            // Handle exceptions and return an error response if needed
            return "An error occurred: " + e.getMessage();
        }
    }
}
