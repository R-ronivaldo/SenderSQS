package com.sendersqs.sender.controller;

import java.util.HashMap;
import java.util.Map;

import com.sendersqs.sender.model.User;
import com.sendersqs.sender.services.SendEmail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SQSController {

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
	private SendEmail sendEmail;

    @Value("${cloud.aws.end-point.uri}")
    private String endPoint;

    @GetMapping("/put/{msg}")
    public void putMessagedToQueue(@PathVariable("msg") String message) {
        queueMessagingTemplate.send(endPoint, MessageBuilder.withPayload(message).build());
    }

    @SqsListener("MyQueueExample.fifo")
    public void loadMessagesFromQueue(String message) {
        Map<String, String> myMap = convertPayloadToMap(message);

        User user = new User();
        user.setId(myMap.get("id"));
        user.setUsername(myMap.get("username"));
        user.setEmail(myMap.get("email"));
        
        System.out.println("Queue Messages: " + message);

        sendEmail.sendEmail(user.getEmail(), ""+user.getId()+user.getUsername()+"","Test Java");
    }

    private Map<String, String> convertPayloadToMap(String payload){
        Map<String, String> myMap = new HashMap<String, String>();
        String result = payload.replace("{", "").replace("}", "").replace("\"", "");
        System.out.println("result: " + result);
        String[] pairs = result.split(",");
        for (int i=0;i<pairs.length;i++) {
            String pair = pairs[i];
            String[] keyValue = pair.split(":");
            myMap.put(keyValue[0], keyValue[1]);
        }
        return myMap;
    }
}