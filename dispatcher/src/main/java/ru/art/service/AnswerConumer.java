package ru.art.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface AnswerConumer {
    void consume(SendMessage sendMessage);
}
