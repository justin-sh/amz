package com.lu.justin.tool.file.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    /**
     * init the prepare work
     */
    void init();

    /**
     * load all files
     * @return Stream<Path>
     */
    Stream<Path> loadAll();

    /**
     * load one file from filename
     * @param filename file name
     * @return file path
     */
    Path load(String filename);

    /**
     * load one file as Resource
     * @param filename file name
     * @return file resource
     */
    Resource loadAsResource(String filename);

    /**
     * store uploaded file to store
     * @param file uploaded file
     */
    void store(MultipartFile file);

    /**
     * delete all files
     */
    void deleteAll();
}
