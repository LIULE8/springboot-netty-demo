package com.client.config;

import com.core.protocol.protobuf.MessageBase;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class NettyClient {

  private NioEventLoopGroup group;
  private SocketChannel channel;

  @Autowired private ClientNettyProperties clientNettyProperties;

  @SneakyThrows
  @PostConstruct
  public void start() {
    group = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap();

    bootstrap
        .option(ChannelOption.SO_KEEPALIVE, clientNettyProperties.getKeepAlive())
        .option(ChannelOption.TCP_NODELAY, clientNettyProperties.getTcpNodelay())
        .option(ChannelOption.SO_SNDBUF, clientNettyProperties.getSndbuf())
        .option(ChannelOption.SO_RCVBUF, clientNettyProperties.getRcvbuf());

    bootstrap
        .group(group)
        .channel(NioSocketChannel.class)
        .remoteAddress(clientNettyProperties.getRemoteHost(), clientNettyProperties.getRemotePort());

        bootstrap.handler(new NettyClientHandlerInitializer());

    ChannelFuture channelFuture = bootstrap.bind().sync();
    channelFuture.addListener(
        (ChannelFutureListener)
            future -> {
              if (future.isSuccess()) {
                log.info("连接 netty server 成功");
              } else {
                log.info("连接失败，进行断线重连");
                future.channel().eventLoop().schedule(this::start, 20, TimeUnit.SECONDS);
              }
            });

    channel = (SocketChannel) channelFuture.channel();
  }

  @PreDestroy
  public void destroy() {}

  public void sendMsg(MessageBase.Message message) {
    channel.writeAndFlush(message);
  }
}
