package com.mall.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017-5-2.
 */
public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    //通过PropertiesUtil工具类读取配置文件
    private static final String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static final String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static final String ftpPwd = PropertiesUtil.getProperty("ftp.pass");
    private static final Integer FTP_PORT = 21;
    private static final String ftpImgPath = "img"; //ftp服务器上文件保存路径

    public String ip;   //服务器Ip
    public int port;    //服务器端口号
    public String user; //连接服务器用户名
    public String pwd;  //连接服务器密码
    public FTPClient ftpClient;

    public  FileUtil(String ip,int port,String user,String pwd){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    /**
     *  文件上传方法
     * @param fileList 上传文件的列表
     * @return
     * @throws IOException
     */
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FileUtil fileUtil = new FileUtil(ftpIp,FTP_PORT,ftpUser,ftpPwd);
        logger.info("已连接服务器，开始上传文件");
        boolean result = fileUtil.uploadFile(fileList,ftpImgPath);
        logger.info("上传文件完毕，上传结果：{}" ,result);
        return result;
    }
    /**
     * 连接ftp服务器，开始上传文件
     * @param fileList 上传文件的列表
     * @param remotePath 保存文件的服务器路径
     * @return
     * @throws IOException
     */
    private boolean uploadFile(List<File> fileList,String remotePath) throws IOException {
        boolean upload = false;
        FileInputStream fis = null;
        //连接ftp
        if( connectServer(this.ip,this.port,this.user,this.pwd) ){
            try {
                ftpClient.changeWorkingDirectory(remotePath);  //切换工作目录
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();  //使用被动模式
                for (File fileItem : fileList){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
                upload = true;
            } catch (IOException e) {
                logger.error("上传文件错误" + e);
            }finally {
                fis.close();
                ftpClient.disconnect();
            }
            logger.info("upload{}",upload);
        }
        return upload;
    }
    /**
     * 连接FTP服务器
     * @param ip
     * @param port
     * @param user
     * @param pwd
     * @return
     */
    private boolean connectServer(String ip,int port,String user,String pwd){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pwd);
        } catch (IOException e) {
           logger.error("连接FTP服务器错误" + e);
        }
        return isSuccess;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
