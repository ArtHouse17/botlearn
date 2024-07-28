package ru.art.service;

import org.springframework.core.io.FileSystemResource;
import ru.art.entity.AppDocument;
import ru.art.entity.AppPhoto;
import ru.art.entity.BinaryContent;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
}
