package com.mall.service.impl;

import com.google.common.collect.Lists;
import com.mall.service.IFileService;
import com.mall.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2017-5-2.
 */
@Service("iFileService")
public class FielServiceImpl implements IFileService {

    private static Logger logger = LoggerFactory.getLogger(FielServiceImpl.class);

    /**
     *  文件上传
     * @param file
     * @param path
     * @return
     */
    public String upload(MultipartFile file,String path){
        //获取上传文件的原名
        String fileName = file.getOriginalFilename();
        //切割上传文件的后缀名
        String extentionFileName = fileName.substring(fileName.lastIndexOf(".")+1);
        //创建上传文件的唯一名称，避免重复
        String uploadFileName = new StringBuilder().append(UUID.randomUUID().toString())
                .append(".").append(extentionFileName).toString();
        //创建目标文件夹
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
            logger.info("创建 "+path+" 文件夹");
        }
        File targetFile = new File(path,uploadFileName);
        logger.info("创建 "+targetFile+" 唯一文件");
        //使用Spring提供的上传方法
        try {
            file.transferTo(targetFile);
            FileUtil.uploadFile(Lists.newArrayList(targetFile));
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件出错" + e);
            return null;
        }
        logger.info("上传文件，上传的文件名{},上传的路径{},文件扩展名{},唯一文件名{}"
        ,fileName,path,extentionFileName,uploadFileName);
        return targetFile.getName();
    }
}
