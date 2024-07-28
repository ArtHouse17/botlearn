package ru.art.service.impl;


import org.hashids.Hashids;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import ru.art.dao.AppDocumentDAO;
import ru.art.dao.AppPhotoDAO;
import ru.art.dao.BinaryContentDAO;
import ru.art.entity.AppDocument;
import ru.art.entity.AppPhoto;
import ru.art.entity.BinaryContent;
import ru.art.service.FileService;
import ru.art.exeptions.UploadFileException;
import ru.art.service.enums.LinkType;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class FileServiceImpl implements FileService {

    @Value("${token}")
    private String token;

    @Value("${service.file_info.uri}")
    private String fileInfoUri;

    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    @Value("${link.addres}")
    private String linkAddress;

    private final AppDocumentDAO appDocumentDAO;

    private final AppPhotoDAO appPhotoDAO;

    private final BinaryContentDAO binaryContentDAO;

    private final Hashids hashids;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO, BinaryContentDAO binaryContentDAO, Hashids hashids) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.binaryContentDAO = binaryContentDAO;
        this.hashids = hashids;
    }

    @Override
    public AppDocument processDoc(Message externalMessage) {
        var telegramDoc = externalMessage.getDocument();
        String fileID = externalMessage.getDocument().getFileId();
        ResponseEntity<String> response = getFilePath(fileID);
        if (response.getStatusCode() == HttpStatus.OK){
            BinaryContent persientBinaryContent = getPersistentBinaryContent(response);
            AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persientBinaryContent);
            return appDocumentDAO.save(transientAppDoc);
        }else{
            throw new UploadFileException("Bad response from service" + response);
        }
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentDAO.save(transientBinaryContent);
    }

    private String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        int lastPhoto = telegramMessage.getPhoto().size()-1;
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(lastPhoto);
        String fileID = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileID);
        if (response.getStatusCode() == HttpStatus.OK){
            BinaryContent persientBinaryContent = getPersistentBinaryContent(response);
            AppPhoto transientAppPhoto = buildTransientAppPhoto(telegramPhoto, persientBinaryContent);
            return appPhotoDAO.save(transientAppPhoto);
        }else{
            throw new UploadFileException("Bad response from service" + response);
        }
    }

    @Override
    public String generateLink(long docId, LinkType linkType) {
        var hash = hashids.encode(docId);
        return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramDoc, BinaryContent persientBinaryContent) {
        return AppPhoto.builder()
                .telegramFieldId(telegramDoc.getFileId())
                .binaryContent(persientBinaryContent)
                .fileSize(telegramDoc.getFileSize())
                .build();
    }

    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persientBinaryContent) {
        return AppDocument.builder()
                .telegramFieldId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persientBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                        .build();
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}",token).replace("{filePath}", filePath);
        URL urlObj = null;
        try{
            urlObj = new URL(fullUri);
        }catch (MalformedURLException e){
            throw new UploadFileException(e);
        }
        try (InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        }catch (IOException e){
            throw new UploadFileException(urlObj.toExternalForm(),e);
        }
    }

    private ResponseEntity<String> getFilePath(String fileID) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token, fileID
        );
    }
}
