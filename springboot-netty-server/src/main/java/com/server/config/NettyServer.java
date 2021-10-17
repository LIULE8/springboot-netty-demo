package com.server.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

@Component
@Slf4j
public class NettyServer {
  private NioEventLoopGroup bossGroup;
  private NioEventLoopGroup workerGroup;

  @Autowired private ServerNettyProperties serverNettyProperties;

  /** @PostConstruct 注解来表示该方法在 Spring 初始化 NettyServer类后调用 */
  @PostConstruct
  @SneakyThrows
  public void start() {
    // init group
    bossGroup = new NioEventLoopGroup(serverNettyProperties.getBossThreadNum());
    workerGroup = new NioEventLoopGroup(serverNettyProperties.getWorkerThreadNum());
    // init serverBootstrap
    ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap
        // 设置bossGroup的配置
        // 1. 设置服务端可连接队列数，对应TCP/IP协议里面listen函数中的backlog参数
        .option(ChannelOption.SO_BACKLOG, serverNettyProperties.getBossBacklog())
        // 设置workerGroup配置
        // 1. 设置tcp长连接，如果两个小时内没有数据通信时，TCP会自动发送一个活动探测数据报文
        .childOption(ChannelOption.SO_KEEPALIVE, serverNettyProperties.getWorkerKeepAlive())
        // 2. 设置是否开启nodelay算法，将小的TCP包组合成一个大的tcp包，200ms的间隔时间发送一次
        .childOption(ChannelOption.TCP_NODELAY, serverNettyProperties.getWorkerTcpNodelay())
        // 3. 设置发送缓冲区
        .childOption(ChannelOption.SO_SNDBUF, serverNettyProperties.getWorkerSndbuf())
        // 4.设置接受缓冲区
        .childOption(ChannelOption.SO_RCVBUF, serverNettyProperties.getWorkerRcvbuf())
        // 4. 设置重用地址
        .childOption(ChannelOption.SO_REUSEADDR, serverNettyProperties.getWorkerReuseaddr())
        // 5. 设置重用端口
        .childOption(EpollChannelOption.SO_REUSEPORT, serverNettyProperties.getWorkerReuseport())
        // 6. 设置开辟内存池
        .childOption(EpollChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

    serverBootstrap
        // 绑定bossGroup和workerGroup
        .group(bossGroup, workerGroup)
        // 指定channel
        .channel(NioServerSocketChannel.class)
        // 指定端口设置socket地址
        .localAddress(new InetSocketAddress(serverNettyProperties.getPort()));

    serverBootstrap
        // 设置 boss的handler
        .handler(new LoggingHandler())
        // 设置 worker 的handler
        .childHandler(new NettyServerHandlerInitializer());

    ChannelFuture future = serverBootstrap.bind().sync();
    if (future.isSuccess()) {
      log.info("启动 netty server 成功");
    }
  }

  @PreDestroy
  @SneakyThrows
  public void destroy() {
    bossGroup.shutdownGracefully().sync();
    workerGroup.shutdownGracefully().sync();
    log.info("关闭 netty server");
  }
}
