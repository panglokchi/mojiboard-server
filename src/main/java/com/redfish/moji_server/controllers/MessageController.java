package com.redfish.moji_server.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redfish.moji_server.models.Message;
import com.redfish.moji_server.repositories.MessageRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;;

@Controller
@CrossOrigin(origins = {"http://localhost:3000", "http://172.26.87.217:3000", "https://mojichan.com"})
@RequestMapping(path="/message") 
public class MessageController {
    @Autowired
    private MessageRepository messageRepository;

    @Value("${gemini_api_key}")
    private String geminiApiKey;

    @PostMapping(path="/add") // Map ONLY POST Requests
    public ResponseEntity<HttpStatus> addNewUser (@Valid @RequestBody NewMessage newMessage)
    {
        Message message = new Message();
        message.setTitle(newMessage.title);
        message.setContent(newMessage.content);
        message.setTime(newMessage.time);

        if(newMessage.parentId != null) {
            Message parentMessage = messageRepository.findById(newMessage.parentId).orElseThrow();
            message.setParent(parentMessage);
        } else {
            message.setParent(null);
        }

        messageRepository.save(message);
        return ResponseEntity.ok().build();
    }

    public static class NewMessage {
        @NotNull private String title;
        @NotNull private String content;
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) private LocalDateTime time;
        private Integer parentId = null;
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public LocalDateTime getTime() { return time; }
        public void setTime(LocalDateTime time) { this.time = time; }
        public Integer getParentId() { return parentId; }
        public void setParentId(Integer parentId) { this.parentId = parentId; }
    }

    /*
    @GetMapping(path="/all")
    public @ResponseBody Iterable<Message> getAllMessages() {
        // This returns a JSON or XML with the users
        return messageRepository.findByParent(null);
    }
     */

    @GetMapping(path="/top")
    public @ResponseBody Iterable<Message> getTopMessages(@RequestParam(required = false) Integer parentId) {
        // This returns a JSON or XML with the users
        if (parentId == null) return messageRepository.findTopLevelMessages();
        return messageRepository.findMessagesByParentId(parentId);
    }
    
    @PostMapping(path="/translate", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getTranslation(@Valid @RequestBody TranslationRequest translationRequest) {

        RestTemplate restTemplate = new RestTemplate();

        String uri = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        //headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

        JSONObject instruction = new JSONObject();
        //instruction.put("thought", true);
        instruction.put("text", "Convert the following text to Emoji, preserving sentence structure using spaces.");
        JSONObject text = new JSONObject();
        text.put("text", translationRequest.getContent());
        //JSONObject text = new JSONObject();
        //text.put("text", "Convert the following to Emoji, preserving sentence structure using spaces：" + translationRequest.getContent());
        JSONArray textArray = new JSONArray();
        textArray.put(instruction);
        textArray.put(text);
        JSONObject parts = new JSONObject();
        parts.put("parts", textArray);
        JSONArray partsArray = new JSONArray();
        partsArray.put(parts);
        JSONObject contents = new JSONObject();
        contents.put("contents", partsArray);

        //System.out.print(contents);

        HttpEntity<String> entity = new HttpEntity<String>(contents.toString(), headers);
        ResponseEntity<String> res =
                restTemplate.postForEntity(uri, entity, String.class);


        try {
            String jsonString = res.getBody();
            TranslationResponse return_content = new ObjectMapper().readValue(jsonString, TranslationResponse.class);   
            String translationText = return_content.candidates.get(0).content.parts.get(0).text;

            JSONObject returnJSON = new JSONObject();
            returnJSON.put("text", translationText);
            return returnJSON.toString();
            
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public static class TranslationRequest {
        @NotNull private String content;
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class TranslationResponse{
        public ArrayList<Candidate> candidates;
        public UsageMetadata usageMetadata;
        public String modelVersion;
        public String responseId;
    }

    public static class Candidate{
        public Content content;
        public String finishReason;
        public double avgLogprobs;
    }

    public static class CandidatesTokensDetail{
        public String modality;
        public int tokenCount;
    }

    public static class Content{
        public ArrayList<Part> parts;
        public String role;
    }

    public static class Part{
        public String text;
    }

    public static class PromptTokensDetail{
        public String modality;
        public int tokenCount;
    }

    public static class UsageMetadata{
        public int promptTokenCount;
        public int candidatesTokenCount;
        public int totalTokenCount;
        public ArrayList<PromptTokensDetail> promptTokensDetails;
        public ArrayList<CandidatesTokensDetail> candidatesTokensDetails;
    }
}
