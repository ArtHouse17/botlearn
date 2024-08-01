package ru.art.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.art.entity.AppDocument;

@Primary
@Repository
public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
