package cn.cjh.manager.controller;

import cn.cjh.core.entity.Result;
import cn.cjh.core.util.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;//文件服务器地址

    @RequestMapping("/uploadFile")
    public Result upload(MultipartFile file){
        //1，取文件的扩展名
        String originalFilename = file.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        try{
            //2创建一个FastDFS的客户端
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            //3执行上传处理
            String path = fastDFSClient.uploadFile(file.getBytes(), substring);
            //4拼接返回的的URL和ip地址，拼装成完整的URL
            String url = FILE_SERVER_URL+path;
            System.out.println(url);
            return new Result(true,url);
        }catch (Exception e){
            return new Result(false,"上传失败");
        }
    }
}
