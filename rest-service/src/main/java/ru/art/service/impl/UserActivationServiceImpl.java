package ru.art.service.impl;


import org.springframework.stereotype.Service;
import ru.art.dao.AppUserDAO;
import ru.art.service.UserActivationService;

import ru.art.utils.Decoder;

@Service
public class UserActivationServiceImpl implements UserActivationService {

    private final AppUserDAO appUserDAO;

    private final Decoder decoder;

    public UserActivationServiceImpl(AppUserDAO appUserDAO, Decoder decoder) {
        this.appUserDAO = appUserDAO;
        this.decoder = decoder;
    }


    @Override
    public boolean activation(String cryptoUserId) {
        var userId = decoder.idOf(cryptoUserId);
        var optional = appUserDAO.findById(userId);
        if (optional.isPresent()){
            var user = optional.get();
            user.setActive(true);
            appUserDAO.save(user);
            return true;
        }
        return false;
    }
}
