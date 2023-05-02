package com.example.myhome.home.controller.socket;

import com.example.myhome.home.dto.CashBoxDTO;
import com.example.myhome.home.model.CashBox;
import com.example.myhome.home.model.Message;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Log
public class WebsocketController {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/cashbox")
    @SendTo("/topic/cashbox")
    public CashBoxDTO cashboxItemMessage(CashBoxDTO dto) throws Exception {
        Thread.sleep(1000);
        return dto;
    }

    public void sendCashboxItem(CashBoxDTO item) {
        log.info("Trying to send new cashbox item to the client...");
        this.template.convertAndSend("/topic/cashbox", item);
    }

    public void sendCashboxItem(CashBox item) {
        log.info("Trying to send new cashbox item to the client...");
        this.template.convertAndSend("/topic/cashbox", item);
    }

    @MessageMapping("/messages")
    @SendTo("/topic/messages")
    public Message messagesItemMessage(Message msg) throws Exception {
        Thread.sleep(1000);
        return msg;
    }

    public void sendMessagesItem(Message item) {
        log.info("Trying to send new message item to the client...");
        System.out.println("catch");
        this.template.convertAndSend("/topic/messages", item);
    }

}
