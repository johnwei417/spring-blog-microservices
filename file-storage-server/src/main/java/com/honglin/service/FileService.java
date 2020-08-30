package com.honglin.service;

import com.honglin.entity.File;

import java.util.List;

public interface FileService {
    /**
     * save file
     *
     * @param file
     * @return
     */
    File saveFile(File file);

    /**
     * delete file
     *
     * @param id
     * @return
     */
    void removeFile(String id);

    /**
     * 根据id获取文件
     *
     * @param
     * @return
     */
    File getFileById(String id);

    /**
     * list files with pagination
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<File> listFilesByPage(int pageIndex, int pageSize);
}

