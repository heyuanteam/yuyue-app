package com.yuyue.app.utils;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


/**
 * @author ：冉野
 * @date ：Created in 2019-07-12 09:29
 * @description：测试图片压缩 测试使用写博客用 需求背景说明 最近后端管理项目中需要用到用户一些证件图片进行表单文件的上传 如果每个人的证件照片都非常大，对服务器资源将是一种浪费，
 * 因为用户量也不是很大，所以也没对接第三方的OSS或者七牛云存储对象，就写个简单的图像压缩吧，我主要是提供了两种方式进行图片压缩，第一种是使用Java自带的Image绘制图像，输入指定的宽高尺寸，最后
 * 在重新绘制新的图片，核心代码也就三四行左右，压缩绘制算法都是JDK底层已经封装完成了。
 * 第二种是使用Google的压缩工具类Thumbnails 大名鼎鼎的不一般，只需要一行代码即可，可实现的功能那是相当的丰富，图像指定宽高压缩，原图压缩，添加水印，图像旋转，指定图像压缩质量比例等等。
 * @modified By：不要重复造轮子 不要重复造轮子 不要重复造轮子
 * @version: 0.0.1$
 */
@Slf4j
public class ImageCompress {

    // 这链接地址是拼接对应的服务器地址 用于回显给前端该图片地址使用。最后返回的效果是这样的：https://ucr.nb01.xyz/image-ucr/700393939333.png
    // 前端通过该地址可在浏览器直接访问，需要说明一点是 图片上传在了/opt/image目录下，最后通过地址能访问到该图片，需要通过nginx做一层转发，具体那就是运维可以配置一下或者自己NGINX 配置一下。
    private static final String PROD_URL = "";
    //private static final String TEST_URL = "http://ucr.nb01.xyz/image-ucr/";
    private static final String REMOTE_URL=  "http://101.37.252.177:82/compression_diagram/";
    private static final String LOCAL_URL = "http://127.0.0.1:8802/feo/";
    // 用的MAC系统，所以直接放在了系统的根目录某个文件夹下 如果是Win系统也可指定在某个盘符下存储即可。
    private static final String dirPath = "/opt/image";
    // 方式一压缩https:
//    public static String imageCompress(MultipartFile file, int targetWidth, int targetHeight) {
//        long startTime = System.currentTimeMillis();
//        //得到上传时的原文件名
//        String originalFilename = file.getOriginalFilename();
//        //获取文件后缀名
//        String suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
//        String imageType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
//        //通过系统时间戳来命名文件：不是非常的规范请勿模仿
//        String name = String.valueOf(System.nanoTime());
//        String saveName = name.concat(suffixName);
//        //存储目录
//        String savePath = UploadPathUtils.getPicUploadDir(targetWidth, targetHeight);
//        //图片存储全路径
//        String outputPath = savePath.concat("/").concat(saveName);
//        log.info("图片存储路径:[{}]",outputPath);
//        OutputStream fouts = null;
//        // 以上都是前期的准备
//        try {
//            //读取源图
//            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
//            // 获取源图宽度
//            double srcWidth = bufferedImage.getWidth();
//            // 获取源图高度
//            double srcHeight = bufferedImage.getHeight();
//            // 判断是否是需要压缩的尺寸比原图小 否则不进行压缩
//            if ((int) srcWidth >= targetWidth && (int) srcHeight >= targetHeight) {
//                // 建立传输通道---文件输出流 最后以流的形式把文件内容传输过去
//                fouts = new FileOutputStream(outputPath);
//                // 绘制新图时，使用Image.SCALE_SMOOTH算法，压缩后的图片质量相对比较光滑，没有明显的锯齿形,又叫做图片压缩光滑算法
//                // 还可选择其他压缩算法 例如：SCALE_FAST 比光滑算法更快速，还有默认算法等可选。
//                Image image = bufferedImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
//                BufferedImage bufferedImage1 = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
//                Graphics g = bufferedImage1.getGraphics();
//                g.setColor(Color.RED);
//                //绘制处理后的图
//                g.drawImage(image, 0, 0, null);
//                g.dispose();
//                ImageIO.write(bufferedImage1, imageType, fouts);
//                log.info("图片压缩结束");
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fouts != null) {
//                try {
//                    fouts.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        if (FileUtil.isLinux()) {
//            outputPath = TEST_URL.concat(saveName);
//        }
//        return outputPath;
//    }

    /**
     * 方式二压缩 Google大法 因为Thumbnails.of() 方法是一个重载方法，参数不仅仅局限于是一个文件类型 可以是以流的形式 File形式，ImageBuffer对象，URL路径,String类型指定文件路径
     * 然后可通过链式构造器传入不通参数，压缩比例，指定输出的格式等最终通过toFile("文件存储路径")返回一个已经压缩完成的图片。
     * @param file 待压缩的文件
     * @return 压缩后图片路径 这个可自己指定
     */
    public static String thumbnail(MultipartFile file) {
        //得到上传时的原文件名
        String originalFilename = file.getOriginalFilename();
        //获取文件格式
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        //获取uuid作为文件名
        String name = UUID.randomUUID().toString().replaceAll("-", "");
        String path = System.getProperty("user.dir");
        System.out.println(path);
        try {
            // 先尝试压缩并保存图片
            Thumbnails.of(file.getInputStream()).scale(1f)
                    .outputQuality(0.015f)
                    .outputFormat("jpg")
                    .toFile("/var/www/html/compression_diagram/" + name);
        } catch (IOException e) {

        }
        String thumbnail = REMOTE_URL.concat(name).concat(".").concat("jpg");
        System.out.println(thumbnail);
        return thumbnail;
    }
}
