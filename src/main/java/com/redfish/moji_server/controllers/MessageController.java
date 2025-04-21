package com.redfish.moji_server.controllers;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.redfish.moji_server.models.Message;
import com.redfish.moji_server.repositories.MessageRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;;

@Controller
@CrossOrigin(origins = {"http://localhost:3000", "http://172.26.87.217:3000"})
@RequestMapping(path="/message") 
public class MessageController {
    @Autowired
    private MessageRepository messageRepository;

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
}
