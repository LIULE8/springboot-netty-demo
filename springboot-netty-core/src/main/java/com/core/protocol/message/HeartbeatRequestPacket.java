package com.core.protocol.message;

import com.core.protocol.Packet;
import com.core.protocol.message.command.Command;

public class HeartbeatRequestPacket extends Packet {

  @Override
  public Byte getCommand() {
    return Command.HEARTBEAT_REQUEST;
  }
}
