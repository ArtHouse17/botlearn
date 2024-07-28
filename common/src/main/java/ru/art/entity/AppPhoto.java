package ru.art.entity;

import jakarta.persistence.*;
import lombok.*;


@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_photo")
public class AppPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String telegramFieldId;
    @OneToOne
    private BinaryContent binaryContent;
    private Integer fileSize;
}
