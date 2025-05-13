package com.register.wowlibre.infrastructure.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "teleport")
@NoArgsConstructor
public class TeleportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "img_url")
    private String imgUrl;
    @Column(name = "position_x")
    private float positionX;
    @Column(name = "position_y")
    private float positionY;
    @Column(name = "position_z")
    private float positionZ;
    private float map;
    private float orientation;
    private float zona;
    private float area;
}
