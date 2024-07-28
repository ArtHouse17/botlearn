package ru.art.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.art.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto,Long> {
}
