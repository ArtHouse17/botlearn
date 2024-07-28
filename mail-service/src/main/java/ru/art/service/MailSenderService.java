package ru.art.service;


import ru.art.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
