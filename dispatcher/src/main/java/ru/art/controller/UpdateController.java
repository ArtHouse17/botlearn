package ru.art.controller;

import lombok.var;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.art.service.UpdateProducer;
import ru.art.utils.MessageUtils;

import static ru.art.model.RabbitQueue.*;


@Component
public class UpdateController {
    private TelegramBot telegramBot;

    private MessageUtils messageUtils;

    private UpdateProducer updateProducer;


    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }
    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update==null) {
            return;
        }

        if (update.hasMessage()) {
            distributeMessagesByType(update);
        }else{
            System.out.println("Error");
        }
    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()) {
            processTextMessage(update);
        }else if (message.hasDocument()){
            processDocMessage(update);
        }else if (message.hasPhoto()){
            processPhotoMessage(update);
        }else{
            setUnsupportedMessageTyoe(update);
        }


    }

    private void setUnsupportedMessageTyoe(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Неподдерживаемый тип");
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendRplymsg(sendMessage);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.producer(PHOTO_MESSAGE_UPDATE, update);
        setFileIsRecivedViev(update);
    }


    private void processDocMessage(Update update) {
        updateProducer.producer(DOC_MESSAGE_UPDATE, update);
        setFileIsRecivedViev(update);
    }

    private void setFileIsRecivedViev(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Файл в обработке");
        setView(sendMessage);
    }

    private void processTextMessage(Update update) {
        updateProducer.producer(TEXT_MESSAGE_UPDATE, update);
    }
}
