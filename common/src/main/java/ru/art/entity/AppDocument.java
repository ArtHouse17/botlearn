package ru.art.entity;

import javax.persistence.*;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "App_document")
@Entity
public class AppDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String telegramFieldId;
    private String docName;
    @OneToOne
    @JoinColumn(name = "binary_content_id")
    private BinaryContent binaryContent;
    private String mimeType;
    private Long fileSize;
}
