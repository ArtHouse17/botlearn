package ru.art.service.impl;

import org.springframework.stereotype.Service;
import ru.art.dao.AppDocumentDAO;
import ru.art.dao.AppPhotoDAO;
import ru.art.entity.AppDocument;
import ru.art.entity.AppPhoto;
import ru.art.service.FileService;
import ru.art.utils.CryptoTool;
import ru.art.utils.Decoder;

@Service
public class FileServiceImpl implements FileService {

    private final AppDocumentDAO appDocumentDAO;

    private final AppPhotoDAO appPhotoDAO;

    private final Decoder decoder;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO, Decoder decoder) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.decoder = decoder;
    }

    @Override
    public AppDocument getDocument(String hash) {
        var id = decoder.idOf(hash);
        if (id == null){
            return null;
        }
        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String hash) {
        var id = decoder.idOf(hash);
        if (id == null){
            return null;
        }
        return appPhotoDAO.findById(id).orElse(null);
    }


}
