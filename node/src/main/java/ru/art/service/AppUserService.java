package ru.art.service;

import ru.art.entity.AppUser;

public interface AppUserService {
    String registerUser(AppUser appUser);
    String setEmail(AppUser appuser, String email);
}
