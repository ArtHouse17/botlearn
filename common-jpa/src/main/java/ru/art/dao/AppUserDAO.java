package ru.art.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.art.entity.AppUser;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    AppUser findAppUserByTelegramUserId(long id);
}
