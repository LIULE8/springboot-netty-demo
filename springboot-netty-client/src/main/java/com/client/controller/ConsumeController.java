package com.client.controller;

import com.client.config.NettyClient;
import com.core.protocol.protobuf.MessageBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ConsumeController {

  @Autowired private NettyClient nettyClient;

  @GetMapping("send")
  public String send() {
    MessageBase.Message message =
        new MessageBase.Message()
            .toBuilder()
                .setCmd(MessageBase.Message.CommandType.NORMAL)
                .setContent("hello netty")
                .setRequestId(UUID.randomUUID().toString())
                .build();
    nettyClient.sendMsg(message);
    return "send OK";
  }
}
