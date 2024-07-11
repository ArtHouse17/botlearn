package ru.art.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.art.service.UpdateProducer;

@Service
public class UpdateProduserImpl implements UpdateProducer {

    private final RabbitTemplate rabbitTemplate;

    public UpdateProduserImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    @Override
    public void producer(String rabbitQueue, Update update) {
        System.out.println(update.getMessage().getChatId() + ": " + update.getMessage().getText());
        rabbitTemplate.convertAndSend(rabbitQueue, update);
    }
}
