package com.honglin.service.Impl;

import com.honglin.entity.File;
import com.honglin.repository.FileRepository;
import com.honglin.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    public FileRepository fileRepository;

    @Override
    public File saveFile(File file) {
        return fileRepository.save(file);
    }

    @Override
    public void removeFile(String id) {
        fileRepository.deleteById(id);
    }

    @Override
    public File getFileById(String id) {
        return fileRepository.findById(id).get();
    }

    @Override
    public List<File> listFilesByPage(int pageIndex, int pageSize) {
        Page<File> page = null;
        List<File> list = null;

        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "uploadDate"));

        page = fileRepository.findAll(pageable);
        list = page.getContent();
        return list;
    }
}

