package com.lu.justin.tool.file.web;

import com.lu.justin.tool.file.service.StorageFileNotFoundException;
import com.lu.justin.tool.file.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;


@Controller
@RequestMapping(value = "/file")
public class FileUploadController {

    private final static Logger log = LoggerFactory.getLogger(FileUploadController.class);

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("files"
                , storageService.loadAll()
                        .map(path -> MvcUriComponentsBuilder.fromMethodName(
                                FileUploadController.class
                                , "serveFile"
                                , path.getFileName().toString()).build().toString())
                        .collect(Collectors.toList())
        );
        return "index.html";
    }

    @ResponseBody
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        log.info("server file for " + filename);
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @PostMapping
    public String upload(@RequestParam(name = "file") MultipartFile file, RedirectAttributes redirectAttributes) {
        log.info("server file for " + file.getOriginalFilename());
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please add file to upload!");
            return "redirect:/file";
        }

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/file";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
