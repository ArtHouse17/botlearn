package ru.art.service.impl;

import org.hashids.Hashids;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.art.dao.AppUserDAO;
import ru.art.entity.AppUser;
import ru.art.service.AppUserService;
import ru.art.dto.MailParams;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static ru.art.enums.UserState.BASIC_STATE;
import static ru.art.enums.UserState.WAIT_FOR_EMAIL;

@Service
public class AppUserServiceImpl implements AppUserService {
    private final AppUserDAO appUserDAO;
    private final Hashids hashids;

    @Value("${spring.activemq.queues.registration-mail}")
    private String registrationMailQueue;

    private final RabbitTemplate rabbitTemplate;

    public AppUserServiceImpl(AppUserDAO appUserDAO, Hashids hashids, RabbitTemplate rabbitTemplate) {
        this.appUserDAO = appUserDAO;
        this.hashids = hashids;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.isActive()) {
            return "Вы уже зарегистрированы!";
        } else if (appUser.getEmail() != null) {
            return "Вам на почту уже было отправлено письмо. "
                    + "Перейдите по ссылке в письме для подтверждения регистрации.";
        }
        appUser.setState(WAIT_FOR_EMAIL);
        appUserDAO.save(appUser);
        return "Введите, пожалуйста, ваш email:";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException e) {
            return "Введите, пожалуйста, корректный email. Для отмены команды введите /cancel";
        }
        var optional = appUserDAO.findByEmail(email);
        if (optional.isEmpty()) {
            appUser.setEmail(email);
            appUser.setState(BASIC_STATE);
            appUser = appUserDAO.save(appUser);

            var cryptoUserId = hashids.encode(appUser.getId());
            sendRequestToMailService(cryptoUserId,email);

            return "Вам на почту было отправлено письмо."
                    + "Перейдите по ссылке в письме для подтверждения регистрации.";
        } else {
            return "Этот email уже используется. Введите корректный email."
                    + " Для отмены команды введите /cancel";
        }
    }

    private void sendRequestToMailService(String cryptoUserId, String email) {
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        rabbitTemplate.convertAndSend(registrationMailQueue, mailParams);
    }
}