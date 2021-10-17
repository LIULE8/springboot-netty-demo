package com.core.protocol;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Packet {
  /** 版本 */
  private Byte version = 1;

  public abstract Byte getCommand();
}
