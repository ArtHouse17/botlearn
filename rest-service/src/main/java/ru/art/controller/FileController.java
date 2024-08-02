package ru.art.controller;

import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.art.service.FileService;


import java.io.IOException;

@RequestMapping("/file")
@RestController
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
    @RequestMapping(method = RequestMethod.GET, value = "/get-doc")
    public void getDoc(@RequestParam("id") String id, HttpServletResponse response){
        var doc = fileService.getDocument(id);
        if (doc == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ;
        }
        response.setContentType(String.valueOf(MediaType.parseMediaType(doc.getMimeType())));
        response.setHeader("Content-Disposition"
                ,"attachment; filename=" + doc.getDocName());
        response.setStatus(HttpServletResponse.SC_OK);

        var binaryContent = doc.getBinaryContent();
        try{
            var out = response.getOutputStream();
            out.write(binaryContent.getFileAsArrayOfBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-photo")
    public void getPhoto(@RequestParam("id") String id, HttpServletResponse response){
        var photo = fileService.getPhoto(id);
        if (photo == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ;
        }
        response.setContentType(MediaType.IMAGE_JPEG.toString());
        response.setHeader("Content-Disposition"
                ,"attachment");
        response.setStatus(HttpServletResponse.SC_OK);

        var binaryContent = photo.getBinaryContent();
        try{
            var out = response.getOutputStream();
            out.write(binaryContent.getFileAsArrayOfBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
