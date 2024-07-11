package ru.art.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.art.service.ProducerService;

import static ru.art.model.RabbitQueue.ANSWER_MESSAGE;
@Service
public class ProduserServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    public ProduserServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    @Override
    public void produceAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }
}
