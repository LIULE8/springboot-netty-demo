package com.client.handler;

import com.core.protocol.protobuf.MessageBase;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageBase.Message> {

  /**
   * 如果服务端发送消息给客户端，下面方法进行接收信息
   *
   * @param ctx
   * @param msg
   * @throws Exception
   */
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, MessageBase.Message msg) throws Exception {
    log.info("客户端收到消息：{}", msg.toString());
  }

  /**
   * 处理异常，一般将实现异常处理的Handler放在ChannelPipeline的最后
   *
   * <p>这样确保所有入站信息都总是被处理，无论它们发生在什么位置，下面只是简单的关闭channel并打印异常信息
   *
   * @param ctx
   * @param cause
   * @throws Exception
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
