package ru.art.service.impl;

import lombok.var;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.art.service.ConsumerService;
import ru.art.service.MainService;
import ru.art.service.ProducerService;

import static ru.art.model.RabbitQueue.*;

@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    @Override
    public void consumeTextMessageUpdates(Update update) {

        mainService.processTextMessage(update);
        System.out.println(update.getMessage().getChatId() + " Дошел до потребителя " + update.getMessage().getText());
        var message = update.getMessage();
        var sendMessage = new SendMessage();

        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("message.getText()");
    }

    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    @Override
    public void consumePhotoUpdates(Update update) {
        mainService.processPhotoMessage(update);
        System.out.println(update.getMessage().getChatId() + " " + update.getMessage().getPhoto());
        var message = update.getMessage();
        var sendMessage = new SendMessage();

        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("message.getText()");
    }

    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    @Override
    public void consumeDocUpdates(Update update) {
        mainService.processDocMessage(update);
        System.out.println(update.getMessage().getChatId() + " " + update.getMessage().getPhoto());
        var message = update.getMessage();
        var sendMessage = new SendMessage();

        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("message.getText()");
    }
}
