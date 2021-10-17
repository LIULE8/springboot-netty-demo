package com.client.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("netty")
public class ClientNettyProperties {

  private String remoteHost;
  private Integer remotePort;

  private Boolean keepAlive;
  private Boolean tcpNodelay;
  private Integer sndbuf;
  private Integer rcvbuf;
}
