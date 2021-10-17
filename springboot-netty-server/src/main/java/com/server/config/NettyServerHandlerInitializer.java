package com.server.config;

import com.core.protocol.protobuf.MessageBase;
import com.server.handler.NettyServerHandler;
import com.server.handler.ServerIdleStateHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class NettyServerHandlerInitializer extends ChannelInitializer<Channel> {
  @Override
  protected void initChannel(Channel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();
    pipeline
        .addLast(new ServerIdleStateHandler())
        .addLast(new ProtobufVarint32FrameDecoder())
        .addLast(new ProtobufDecoder(MessageBase.Message.getDefaultInstance()))
        .addLast(new ProtobufVarint32LengthFieldPrepender())
        .addLast(new ProtobufEncoder())
        .addLast(new NettyServerHandler());
  }
}
