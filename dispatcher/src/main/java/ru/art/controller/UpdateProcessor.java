package ru.art.controller;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.art.configuration.RabbitConfiguration;
import ru.art.service.UpdateProducer;
import ru.art.utils.MessageUtils;

import java.util.Random;



@Component
public class UpdateProcessor {
    private TelegramBot telegramBot;

    private MessageUtils messageUtils;

    private UpdateProducer updateProducer;

    private final RabbitConfiguration rabbitConfiguration;


    public UpdateProcessor(MessageUtils messageUtils, UpdateProducer updateProducer, RabbitConfiguration rabbitConfiguration) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
        this.rabbitConfiguration = rabbitConfiguration;
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
            System.out.println("UpdateRecived: " + update);
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
        updateProducer.producer(rabbitConfiguration.getPhotoMessageUpdateQueue(), update);
        setFileIsRecivedViev(update);
    }


    private void processDocMessage(Update update) {
        updateProducer.producer(rabbitConfiguration.getDocMessageUpdateQueue(), update);
        setFileIsRecivedViev(update);
    }

    private void setFileIsRecivedViev(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Файл в обработке");
        setView(sendMessage);
    }

    private void processTextMessage(Update update) {
        updateProducer.producer(rabbitConfiguration.getTextMessageUpdateQueue(), update);
    }
}
