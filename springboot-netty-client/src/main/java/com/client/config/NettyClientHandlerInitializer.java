package com.client.config;

import com.client.handler.HeartbeatHandler;
import com.client.handler.NettyClientHandler;
import com.core.protocol.protobuf.MessageBase;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyClientHandlerInitializer extends ChannelInitializer<Channel> {
  @Override
  protected void initChannel(Channel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();
    pipeline
        .addLast(new IdleStateHandler(0, 10, 0))
        .addLast(new ProtobufVarint32FrameDecoder())
        .addLast(new ProtobufDecoder(MessageBase.Message.getDefaultInstance()))
        .addLast(new ProtobufVarint32LengthFieldPrepender())
        .addLast(new ProtobufEncoder())
        .addLast(new HeartbeatHandler())
        .addLast(new NettyClientHandler());
  }
}
