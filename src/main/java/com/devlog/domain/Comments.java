package com.devlog.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(
)
public class Comments {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

}
