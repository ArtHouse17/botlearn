package ru.art.entity;


import lombok.*;

import javax.persistence.*;


@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "App_photo")
@Entity
public class AppPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String telegramFieldId;
    @OneToOne
    private BinaryContent binaryContent;
    private Integer fileSize;
}

