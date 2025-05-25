package com.example.pixel_perfection.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String url;
    private String title;
    private LocalDateTime uploadDate = LocalDateTime.now();
    private Boolean status = true;
    private Integer views = 0;
    private Integer downloads = 0;
    private Boolean is4k = false;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


}
