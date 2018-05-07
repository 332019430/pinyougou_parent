package com.pinyougou.fastdfs.test;



import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.IOException;

/**
 * @author 小郑
 * @version 1.0
 * @description com.pinyougou.fastdfs.test
 * @date 2018/4/27/0027
 */
public class UploadTest {
    @Test
    public void testUpload() throws Exception {
        //1.创建一个配置文件  配置服务端的IP地址和端口

        //2.加载配置文件 初始化
        ClientGlobal.init("G:\\pinyougou_parent\\pinyougou_shop_web\\src\\main\\resources\\config\\fastdfs_client.conf");

        //3.先创建一个trackerClient对象  直接nEW一个即可
        TrackerClient trackerClient = new TrackerClient();


        //4.通过trackerClient对象获取trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //5.定义一个storageServer对象 赋值为null 就可以了
        StorageServer storageServer = null;
        //6.构建一个storageClient对象
        StorageClient storageClient = new StorageClient(trackerServer,storageServer);

        //7.上传图片
        //参数1：本地文件的路径
        //参数2：文件的扩展名  不要带“.”
        //参数3：元数据（图片的像素 大小 高度 时间戳）
        String[] jpgs = storageClient.upload_file("F:\\aheimawork\\1-7前端\\day02HTML_CSS\\day02HTML_CSS\\代码\\web02_HTML_CSS\\img\\images\\small03.jpg", "jpg", null);

        for (String jpg : jpgs) {
            System.out.println(jpg);
        }


    }
}
