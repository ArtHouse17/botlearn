package ru.art.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.art.dao.RawDataDAO;
import ru.art.entity.RawData;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class MainServiceImplTest {
    @Autowired
    private RawDataDAO rawDataDAO;

    @Test
    public void test() {
        Update update = new Update();
        Message msg = new Message();
        msg.setText("ffff");
        update.setMessage(msg);

        RawData rawData = RawData.builder()
                .event(update)
                .build();
        Set<RawData> testData = new HashSet<>();

        testData.add(rawData);
        System.out.println(rawData.getId());
        System.out.println(testData.toString());
        System.out.println(testData.hashCode());
        rawDataDAO.save(rawData);
        System.out.println(rawData.getId());
        System.out.println(testData.toString());
        System.out.println(testData.hashCode());
        Assert.isTrue(testData.contains(rawData), "Not found");
    }
}