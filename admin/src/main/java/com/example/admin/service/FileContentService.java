package com.example.admin.service;


import com.example.admin.config.Launch;
import com.example.admin.entity.FileContent;
import com.example.admin.mapper.FileContentMapper;
import com.example.admin.utils.DateUtils;
import com.example.admin.utils.IdGen;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

@Service(value = "fileContentService")
@Transactional
public class FileContentService {

    private FileContentMapper fileContentMapper;

    /**
     * @param fileContent
     * @return
     * @throws Exception
     */
    public int edit(FileContent fileContent) throws Exception {
        return this.fileContentMapper.insertSelective(fileContent);
    }

    public FileContent fileUpload(MultipartFile file, String remark, String sigin, String userId) throws Exception {
        FileContent fileContent = new FileContent();
        String upFileName = null;
        String upFileType = null;
        String fileViewPath = Launch.newInstance().getFileViewPath();
        fileViewPath += fileViewPath.endsWith("/") ? "" : "/";
        String fileUploadPath = Launch.newInstance().getFileUploadPath();
        fileUploadPath += fileUploadPath.endsWith("/") ? "" : "/";

        fileContent.setFileId(IdGen.uuid());
        /***************************/
        fileContent.setFileName(file.getOriginalFilename());
        // 文件上传时，Chrome和IE/Edge对于originalFilename处理不同
        // Chrome 会获取到该文件的直接文件名称，IE/Edge会获取到文件上传时完整路径/文件名

        // Check for Unix-style path
        int unixSep = fileContent.getFileName().lastIndexOf('/');
        // Check for Windows-style path
        int winSep = fileContent.getFileName().lastIndexOf('\\');
        // Cut off at latest possible point
        int pos = (Math.max(winSep, unixSep));
        if (pos != -1) {
            // Any sort of path separator found...
            fileContent.setFileName(fileContent.getFileName().substring(pos + 1));
        }
        /***************/
        fileContent.setFileSize(file.getSize());

        upFileName = DateUtils.getDate() + "/" + fileContent.getFileId() + fileContent.getFileName().substring(fileContent.getFileName().lastIndexOf("."));

        fileContent.setRemark(remark);
        fileContent.setSigin(sigin);
        fileContent.setUpdateTime(new Date());
        fileContent.setUpdateUser(userId);
        fileContent.setUploadPath(fileUploadPath + upFileName);
        fileContent.setViewPath(fileViewPath + upFileName);

        //存放文件
        File dest = new File(fileContent.getUploadPath());
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        try {
            // 通过ImageReader来解码这个file并返回一个BufferedImage对象
            // 如果找不到合适的ImageReader则会返回null，我们可以认为这不是图片文件
            // 或者在解析过程中报错，也返回false
            BufferedImage image = ImageIO.read(dest);
            if (image != null) {
                upFileType = "img";
                fileContent.setImgWidth((long) image.getWidth());
                fileContent.setImgHeight((long) image.getHeight());
            } else {
                upFileType = "other";
            }

        } catch (IOException ex) {
            upFileType = "other";
        }
        fileContent.setFileType(upFileType);
        this.fileContentMapper.insertSelective(fileContent);


        return fileContent;
    }
}

