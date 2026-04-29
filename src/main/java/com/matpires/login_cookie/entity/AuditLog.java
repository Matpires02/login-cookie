package com.matpires.login_cookie.entity;

import com.matpires.login_cookie.entity.base.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class AuditLog extends Auditable {

    @Id
    @GeneratedValue
    private Long id;

    private String action;
    private String endpoint;
    private String method;
    private String ip;
    private boolean success;
}