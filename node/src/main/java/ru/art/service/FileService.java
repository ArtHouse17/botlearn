package ru.art.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.art.entity.AppDocument;
import ru.art.entity.AppPhoto;
import ru.art.service.enums.LinkType;

public interface FileService{
    AppDocument processDoc(Message externalMessage);
    AppPhoto processPhoto(Message telegramMessage);
    String generateLink(long docId, LinkType linkType);
}
