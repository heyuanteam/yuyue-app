package com.yuyue.app.utils;


import com.github.tobato.fastdfs.domain.FileInfo;
import com.github.tobato.fastdfs.domain.MateData;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.proto.storage.DownloadFileWriter;
import com.github.tobato.fastdfs.proto.storage.StorageDownloadCommand;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.yuyue.app.api.domain.Variables;
import com.yuyue.app.config.CommandTestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class FastdfsUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FastdfsUtils.class);

    File file = null;
    Set<MateData> metaDataSet = null;

    public void newFile() {
        metaDataSet = createMetaData();
//        file = new File(variables.pathname);
    }

    @Autowired
    private CommandTestBase commandTestBase;

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    /**
     * 测试1--图片上传
     */
    public void testUpload() throws FileNotFoundException {
        //上传图片
        StorePath storePath = this.storageClient.uploadFile
                (new FileInputStream(file), file.length(), Variables.fileExtName, metaDataSet);
        printlnPath(storePath);
    }

    /**
     * 测试2--图片上传缩略图
     */
    public void testCrtThumbImage() throws FileNotFoundException {
        //上传图片的缩略图
        StorePath storePath = this.storageClient.uploadImageAndCrtThumbImage
                (new FileInputStream(file), file.length(), Variables.fileExtName, metaDataSet);
        String fullPath = thumbImageConfig.getThumbImagePath(storePath.getFullPath());
//        System.out.println("【图片缩略图带有组名的路径】:" + fullPath);
        printlnPath(storePath);
    }

    /**
     * 查询
     */
//    public void testQuery() {
//        FileInfo fileInfo = this.storageClient.queryFileInfo(Variables.groupName, Variables.path);
//        System.out.println("图片信息如下：\n" + fileInfo.getCrc32() + "\n"
//                + new Date(fileInfo.getCreateTime()) + "\n" + fileInfo.getFileSize()
//                + "\n" + fileInfo.getSourceIpAddr());
//    }

    /**
     * 删除
     */
//    public void testDel() {
//        this.storageClient.deleteFile(Variables.filePath);
//    }


    /**
     * 删除(效果同上删除)
     */
//    public void testDel2() {
//        this.storageClient.deleteFile(Variables.groupName, Variables.path);
//    }


    /**
     * 下载文件
     */
//    public void downLoadFile() {
//        DownloadFileWriter callback = new DownloadFileWriter(Variables.filename);
//        StorageDownloadCommand<String> stringStorageDownloadCommand = new StorageDownloadCommand<>(Variables.groupName, Variables.path, callback);
//        String fileName = commandTestBase.executeStoreCmd(stringStorageDownloadCommand);
//        LOGGER.info("----文件下载成功-----{}", fileName);
//    }

    /**
     * 创建元信息
     *
     * @return
     */
    private Set<MateData> createMetaData() {
        Set<MateData> metaDataSet = new HashSet<>();
        metaDataSet.add(new MateData("Author", "lucifer"));
        metaDataSet.add(new MateData("CreateDate", "2018-11-11"));
        return metaDataSet;
    }


    private void printlnPath(StorePath storePath) {
        //组名
        System.out.println("【组名】:" + storePath.getGroup());
        //带组名的文件地址
        System.out.println("【带组名的文件地址】:" + storePath.getFullPath());
        //不带组名的文件地址
        System.out.println("【不带组名的文件地址】:" + storePath.getPath());
    }
}
