package ru.art.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.art.entity.AppPhoto;

@Repository
public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
