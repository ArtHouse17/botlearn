package ru.art.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.art.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument,Long> {
}
