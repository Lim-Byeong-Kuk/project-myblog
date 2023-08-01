package com.myblog.api.config.data;

import java.util.Date;

public class UserSession {

    public final Long id;

    public final Date issuedAt;
    public final Date expiration;

    public UserSession(Long id, Date issuedAt, Date expiration) {
        this.id = id;
        this.issuedAt = issuedAt;
        this.expiration = expiration;
    }
}
