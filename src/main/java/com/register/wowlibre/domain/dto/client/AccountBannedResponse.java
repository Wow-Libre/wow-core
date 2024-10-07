package com.register.wowlibre.domain.dto.client;

import java.util.*;

public class AccountBannedResponse {
  public final Long id;
  public final Date bandate;
  public final Date unbandate;
  public final String bannedBy;
  public final String banReason;
  public final boolean active;

  public AccountBannedResponse(Long id, Date bandate, Date unbandate, String bannedBy, String banReason, boolean active) {
    this.id = id;
    this.bandate = bandate;
    this.unbandate = unbandate;
    this.bannedBy = bannedBy;
    this.banReason = banReason;
    this.active = active;
  }
}
