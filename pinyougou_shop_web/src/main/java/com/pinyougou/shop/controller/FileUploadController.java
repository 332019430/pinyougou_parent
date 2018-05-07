package com.pinyougou.shop.controller;

import com.pinyougou.utils.FastDFSClient;
import entity.Result;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 小郑
 * @version 1.0
 * @description com.pinyougou.shop.controller
 * @date 2018/4/27/0027
 */
@RestController
public class FileUploadController {
    @Value("${IMAGE_SERVER_URL}")
    private String IMAGE_SERVER_URL;

    @RequestMapping("/upload")
    public Result uploadFile( MultipartFile file){

        try {
            byte[] bytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            if (StringUtils.isEmpty(originalFilename)){
                return new Result(false,"增加失败");
            }
            String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            FastDFSClient client = new FastDFSClient("classpath:config/fastdfs_client.conf");
            String parturl = client.uploadFile(bytes, suffix);
            String url=IMAGE_SERVER_URL+parturl;
            System.out.println(url);
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"增加失败");
    }
}
