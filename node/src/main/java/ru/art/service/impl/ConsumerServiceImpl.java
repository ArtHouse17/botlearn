package ru.art.service.impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.art.service.ConsumerService;
import ru.art.service.MainService;

import static ru.art.model.RabbitQueue.*;

@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @RabbitListener(queues = "${spring.activemq.queues.text-message-update}")
    @Override
    public void consumeTextMessageUpdates(Update update) {
        mainService.processTextMessage(update);
    }

    @RabbitListener(queues = "${spring.activemq.queues.photo-message-update}")
    @Override
    public void consumePhotoUpdates(Update update) {
        mainService.processPhotoMessage(update);
    }

    @RabbitListener(queues = "${spring.activemq.queues.doc-message-update}")
    @Override
    public void consumeDocUpdates(Update update) {
        mainService.processDocMessage(update);
    }
}
