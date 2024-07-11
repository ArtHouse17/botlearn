package ru.art.service.impl;

import lombok.var;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.art.dao.AppDocumentDAO;
import ru.art.dao.AppUserDAO;
import ru.art.dao.RawDataDAO;
import ru.art.entity.AppDocument;
import ru.art.entity.AppPhoto;
import ru.art.entity.AppUser;
import ru.art.entity.RawData;
import ru.art.entity.enums.UserState;
import ru.art.exeptions.UploadFileException;
import ru.art.service.FileService;
import ru.art.service.MainService;
import ru.art.service.ProducerService;
import ru.art.service.enums.ServiceCommands;

import static ru.art.entity.enums.UserState.BASIC_STATE;
import static ru.art.entity.enums.UserState.WAIT_FOR_EMAIL;
import static ru.art.service.enums.ServiceCommands.*;

@Service
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private  final AppUserDAO appUserDAO;
    private final FileService fileService;
    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO, FileService fileService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
    }

    private AppUser findOrSaveAppUser(Update update){
        User user = update.getMessage().getFrom();
        AppUser persientAppUser = appUserDAO.findAppUserByTelegramUserId(user.getId());

        if (persientAppUser == null){
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(user.getId())
                    .userName(user.getUserName())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    // TODO изменение значение по умолчанию после добав рег
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return persientAppUser;
    }

    @Override
    public void processTextMessage(Update update) {
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        var serviceCommand = ServiceCommands.fromValue(text);
        if (CANCEL.equals(serviceCommand)){
            output = cancelProcess(appUser);
        }else if (BASIC_STATE.equals(userState)){
            output = processServiceCommand(appUser, text);
        }else if (WAIT_FOR_EMAIL.equals(userState)){
            // TODO
        }else{
            output = "Ошибка";
        }
        var chatID = update.getMessage().getChatId();
        sendAnswer(output, chatID);
        saveRawData(update);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatID = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatID,appUser)){
            return;
        }
        try{
            AppDocument doc = fileService.processDoc(update.getMessage());
            var answer = "Документ загружен!";
            sendAnswer(answer,chatID);
        }catch(UploadFileException e){
            String eerr = "Загрузка файла не удалась";
            sendAnswer(eerr,chatID);
        }

    }

    private boolean isNotAllowToSendContent(Long chatID, AppUser appUser) {
        var userState = appUser.getState();
        if (!appUser.isActive()){
            var error = "Зарегестрируйтесь!";
            sendAnswer(error,chatID);
            return true;
        }else if (!BASIC_STATE.equals(userState)){
            var error = "/cancel для ввода команды";
            sendAnswer(error,chatID);
            return true;
        }
        return false;
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatID = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatID,appUser)){
            return;
        }
        try{
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            var answer = "Документ загружен!";
            sendAnswer(answer,chatID);
        }catch(UploadFileException e){
            String eerr = "Загрузка файла не удалась";
            sendAnswer(eerr,chatID);
        }
    }

    private void sendAnswer(String output, Long chatID) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String text) {
        if (REGISTRATION.equals(text)){
            //TODO добав регистр
            return "Временно не доступно";
        }else if (HELP.equals(text)){
            return help();
        }else if (START.equals(text)){
            return "Просмотрите список команд";
        }else{
            return "Неизвестная команда - используйте /help";
        }
    }

    private String help() {
        return "Список доступных команд: \n"
                + "/cansel - отмена выполнения текущей команды;\n"
                +"/registration - регистрация пользователя.";

    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отмнена";
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();

        rawDataDAO.save(rawData);
        System.out.println(update.getMessage().getChatId() + " Дошел в MainService " + update.getMessage().getText());
        var message = update.getMessage();
        var sendMessage = new SendMessage();

        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("message.getText()");
        producerService.produceAnswer(sendMessage);
    }
}
