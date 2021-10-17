package com.client.handler;

import com.client.config.NettyClient;
import com.core.protocol.protobuf.MessageBase;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
  @Autowired private NettyClient nettyClient;

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent idleStateEvent = (IdleStateEvent) evt;

      if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
        log.info("已经10s没有发消息给服务端");
        // 向服务端发送心跳包
        MessageBase.Message heartbeat =
            new MessageBase.Message()
                .toBuilder()
                    .setCmd(MessageBase.Message.CommandType.HEARTBEAT_REQUEST)
                    .setRequestId(UUID.randomUUID().toString())
                    .setContent("heartbeat")
                    .build();
        // 发送心跳信息，并在发送失败时关闭该连接
        ctx.writeAndFlush(heartbeat).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
      }
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    // 如果运行过程中服务端挂了，执行重连机制
    EventLoop eventExecutors = ctx.channel().eventLoop();
    eventExecutors.schedule(() -> nettyClient.start(), 10, TimeUnit.SECONDS);
    super.channelInactive(ctx);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.error("捕获异常：{}", cause.getMessage());
    ctx.channel().close();
  }
}
