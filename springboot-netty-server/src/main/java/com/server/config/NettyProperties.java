package com.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "netty")
public class NettyProperties {

  private Integer port;

  private Integer bossThreadNum;
  private Integer bossBacklog;

  private Integer workerThreadNum;
  private Boolean workerKeepAlive;
  private Boolean workerTcpNodelay;
  private Integer workerSndbuf;
  private Integer workerRcvbuf;
  private Boolean workerReuseaddr;
  private Boolean workerReuseport;
}
