package com.taotao.controller;

import com.taotao.utils.FtpUtil;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

public class FTPTest {

    @Test
    public void testFTPClient() throws Exception {
        //创建一个FtpClient对象
        FTPClient ftpClient = new FTPClient();
        //创建ftp链接
        ftpClient.connect("192.168.220.128",21);
        //登录ftp服务器，使用用户名和密码
        ftpClient.login("ftpuser","111");
        //上传文件
        //读取本地文件
        FileInputStream inputStream = new FileInputStream(new File(
                "D:\\Files\\Pictures\\20180802205308622.png"));
        //设置上传的路径
        ftpClient.changeWorkingDirectory("/home/ftpuser/www/images");
        //修改上传文件格式
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        //第一个参数：服务器端文档名
        //第二个参数：上传文档的inputStream
        ftpClient.storeFile("hello1.jpg",inputStream);
        //关闭连接
        ftpClient.logout();
    }

    @Test
    public void testFtpUtil() throws Exception{
        FileInputStream inputStream = new FileInputStream(new File(
                "D:\\Files\\Pictures\\20180802205308622.png"));
        FtpUtil.uploadFile(
                "192.168.220.128",21,"ftpuser",
                "111","/home/ftpuser/www/images",
                "/2020/04/01","hello.jpg",inputStream);
    }
}
