package ru.art.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.art.entity.AppDocument;
import ru.art.entity.AppPhoto;

public interface FileService{
    AppDocument processDoc(Message externalMessage);
    AppPhoto processPhoto(Message telegramMessage);
}
