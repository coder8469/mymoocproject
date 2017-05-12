package com.mall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Administrator on 2017-5-2.
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
