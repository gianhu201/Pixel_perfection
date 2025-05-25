package com.example.pixel_perfection.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "download_packages")
public class DownloadPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private Integer downloadLimit;
    private BigDecimal price;
    private Integer durationDays;
    private LocalDateTime createdAt = LocalDateTime.now();
    private Boolean is4k = false;

}
