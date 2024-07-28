package ru.art.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.art.dto.MailParams;
import ru.art.service.ConsumerService;
import ru.art.service.MailSenderService;

@Service
@RequiredArgsConstructor
public class ConsumerServiceImpl implements ConsumerService {

    private final MailSenderService mailSenderService;

    @RabbitListener(queues = "${spring.rabbitmq.queues.registration-mail}")
    @Override
    public void consumeRegistrationMail(MailParams mailParams) {
        mailSenderService.send(mailParams);
    }
}
