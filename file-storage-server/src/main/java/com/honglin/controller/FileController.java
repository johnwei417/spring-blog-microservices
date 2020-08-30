package com.honglin.controller;

import com.honglin.common.CommonResponse;
import com.honglin.entity.File;
import com.honglin.service.FileService;
import com.honglin.utils.MD5Util;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @Value("${eureka.instance.hostname}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    @RequestMapping(value = "/")
    public CommonResponse index() {
        // show top 20 records
        List<File> files = fileService.listFilesByPage(0, 20);
        return new CommonResponse(HttpStatus.SC_OK, "query files success!", files);
    }

    /**
     * find files with pagination
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping("files/{pageIndex}/{pageSize}")
    @ResponseBody
    public List<File> listFilesByPage(@PathVariable int pageIndex, @PathVariable int pageSize) {
        return fileService.listFilesByPage(pageIndex, pageSize);
    }

    /**
     * get file info
     *
     * @param id
     * @return
     */
    @GetMapping("files/{id}")
    @ResponseBody
    public ResponseEntity<Object> serveFile(@PathVariable String id) {

        File file = fileService.getFileById(id);

        if (file != null) {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + file.getName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .header(HttpHeaders.CONTENT_LENGTH, file.getSize() + "")
                    .header("Connection", "close")
                    .body(file.getContent());
        } else {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("File was not found");
        }

    }

    /**
     * view files at webpage
     *
     * @param id
     * @return
     */
    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseEntity<Object> serveFileOnline(@PathVariable String id) {

        File file = fileService.getFileById(id);

        if (file != null) {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=\"" + file.getName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                    .header(HttpHeaders.CONTENT_LENGTH, file.getSize() + "")
                    .header("Connection", "close")
                    .body(file.getContent());
        } else {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("File was not fount");
        }

    }

    /**
     * upload file
     *
     * @param file
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        try {
            File f = new File(file.getOriginalFilename(), file.getContentType(), file.getSize(), file.getBytes());
            f.setMd5(MD5Util.getMD5(file.getInputStream()));
            fileService.saveFile(f);
        } catch (IOException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("message",
                    "Your " + file.getOriginalFilename() + " is wrong!");
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    /**
     * upload file api
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        File returnFile = null;
        try {
            File f = new File(file.getOriginalFilename(), file.getContentType(), file.getSize(), file.getBytes());
            f.setMd5(MD5Util.getMD5(file.getInputStream()));
            returnFile = fileService.saveFile(f);
            String path = "//" + serverAddress + ":" + serverPort + "/view/" + returnFile.getId();
            return ResponseEntity.status(HttpStatus.SC_OK).body(path);

        } catch (IOException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }

    }

    /**
     * delete file
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteFile(@PathVariable String id) {

        try {
            fileService.removeFile(id);
            return ResponseEntity.status(HttpStatus.SC_OK).body("DELETE Success!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}


