package com.nmeylan.powermode;

public interface Power {
  default PowerMode powerMode() {
    return PowerMode.getInstance();
  }
}
