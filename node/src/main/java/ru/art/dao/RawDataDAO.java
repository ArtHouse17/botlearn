package ru.art.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.art.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
