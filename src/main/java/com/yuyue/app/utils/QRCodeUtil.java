package com.yuyue.app.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.yuyue.app.api.domain.ReturnResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.*;

import static com.google.zxing.client.j2se.MatrixToImageWriter.toBufferedImage;

/**
 * 二维码生成解析工具类
 *
 * @author ly
 * @data 2019-9-18 13:10:14
 */
public class QRCodeUtil {
    private static Logger log = LoggerFactory.getLogger(QRCodeUtil.class);

    // 二维码颜色==黑色
    private static final int BLACK = 0xFF000000;
    // 二维码颜色==白色
    private static final int WHITE = 0xFFFFFFFF;
    // 二维码图片格式==jpg和png两种
    private static final List<String> IMAGE_TYPE = new ArrayList<>();

    //存储路径
//    private static final String UPLOAD ="upload";

    static {
        IMAGE_TYPE.add("jpg");
        IMAGE_TYPE.add("png");
    }

    /**
     * zxing方式生成二维码
     * 注意：
     * 1,文本生成二维码的方法独立出来,返回image流的形式,可以输出到页面
     * 2,设置容错率为最高,一般容错率越高,图片越不清晰, 但是只有将容错率设置高一点才能兼容logo图片
     * 3,logo图片默认占二维码图片的20%,设置太大会导致无法解析
     *
     * @param content  二维码包含的内容，文本或网址
     * @param path     生成的二维码图片存放位置
     * @param size     生成的二维码图片尺寸 可以自定义或者默认（250）
     * @param logoPath logo的存放位置
     */
    public static boolean zxingCodeCreate(String content, String path, Integer size, String logoPath) {
        try {
            //图片类型
            String imageType = "jpg";
            //获取二维码流的形式，写入到目录文件中
            BufferedImage image = getBufferedImage(content, size, logoPath);
            //获得随机数
            Random random = new Random();
            //生成二维码存放文件
            File file = new File(path+random.nextInt(1000)+".jpg");
            if (!file.exists()) {
                file.mkdirs();
            }
            ImageIO.write(image, imageType, file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 二维码流的形式，包含文本内容
     *
     * @param content  二维码文本内容
     * @param size     二维码尺寸
     * @param logoPath logo的存放位置
     * @return
     */
    public static BufferedImage getBufferedImage(String content, Integer size, String logoPath) {
        if (size == null || size <= 0) {
            size = 250;
        }
        BufferedImage image = null;
        try {
            // 设置编码字符集
            Map<EncodeHintType, Object> hints = new HashMap<>();
            //设置编码
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            //设置容错率最高
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);
            // 1、生成二维码
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, size, size, hints);
            // 2、获取二维码宽高
            int codeWidth = bitMatrix.getWidth();
            int codeHeight = bitMatrix.getHeight();
            // 3、将二维码放入缓冲流
            image = new BufferedImage(codeWidth, codeHeight, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < codeWidth; i++) {
                for (int j = 0; j < codeHeight; j++) {
                    // 4、循环将二维码内容定入图片
                    image.setRGB(i, j, bitMatrix.get(i, j) ? BLACK : WHITE);
                }
            }
            //判断是否写入logo图片
            if (logoPath != null && !"".equals(logoPath)) {
                File logoPic = new File(logoPath);
                if (logoPic.exists()) {
                    Graphics2D g = image.createGraphics();
                    BufferedImage logo = ImageIO.read(logoPic);
                    int widthLogo = logo.getWidth(null) > image.getWidth() * 2 / 10 ? (image.getWidth() * 2 / 10) : logo.getWidth(null);
                    int heightLogo = logo.getHeight(null) > image.getHeight() * 2 / 10 ? (image.getHeight() * 2 / 10) : logo.getHeight(null);
                    int x = (image.getWidth() - widthLogo) / 2;
                    int y = (image.getHeight() - heightLogo) / 2;
                    // 开始绘制图片
                    g.drawImage(logo, x, y, widthLogo, heightLogo, null);
                    g.drawRoundRect(x, y, widthLogo, heightLogo, 15, 15);
                    //边框宽度
                    g.setStroke(new BasicStroke(2));
                    //边框颜色
                    g.setColor(Color.WHITE);
                    g.drawRect(x, y, widthLogo, heightLogo);
                    g.dispose();
                    logo.flush();
                    image.flush();
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 给二维码图片添加Logo
     *
     * @param qrPic   二维码图片
     * @param logoPic logo图片
     * @param path    合成后的图片存储目录
     */
    public static boolean zxingCodeCreate(File qrPic, File logoPic, String path) {
        try {
            String imageType = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
            if (!IMAGE_TYPE.contains(imageType)) {
                return false;
            }

            if (!qrPic.isFile() && !logoPic.isFile()) {
                return false;
            }

            //读取二维码图片，并构建绘图对象

            BufferedImage image = ImageIO.read(qrPic);
            Graphics2D g = image.createGraphics();
            //读取Logo图片
            BufferedImage logo = ImageIO.read(logoPic);
            //设置logo的大小,最多20%0
            int widthLogo = logo.getWidth(null) > image.getWidth() * 2 / 10 ? (image.getWidth() * 2 / 10) : logo.getWidth(null);
            int heightLogo = logo.getHeight(null) > image.getHeight() * 2 / 10 ? (image.getHeight() * 2 / 10) : logo.getHeight(null);
            // 计算图片放置位置，默认在中间
            int x = (image.getWidth() - widthLogo) / 2;
            int y = (image.getHeight() - heightLogo) / 2;
            // 开始绘制图片
            g.drawImage(logo, x, y, widthLogo, heightLogo, null);
            g.drawRoundRect(x, y, widthLogo, heightLogo, 15, 15);
            //边框宽度
            g.setStroke(new BasicStroke(2));
            //边框颜色
            g.setColor(Color.WHITE);
            g.drawRect(x, y, widthLogo, heightLogo);
            g.dispose();
            logo.flush();
            image.flush();
            File newFile = new File(path);
            if (!newFile.exists()) {
                newFile.mkdirs();
            }
            ImageIO.write(image, imageType, newFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 二维码的解析方法
     *
     * @param path 二维码图片目录
     * @return
     */
    public static Result zxingCodeAnalyze(String path) {
        try {
            MultiFormatReader formatReader = new MultiFormatReader();
            File file = new File(path);
            if (file.exists()) {
                BufferedImage image = ImageIO.read(file);
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                Binarizer binarizer = new HybridBinarizer(source);
                BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
                Map hints = new HashMap();
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                Result result = formatReader.decode(binaryBitmap, hints);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取本机Ip
     *
     *  通过 获取系统所有的networkInterface网络接口 然后遍历 每个网络下的InterfaceAddress组。
     *  获得符合 <code>InetAddress instanceof Inet4Address</code> 条件的一个IpV4地址
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String localIp(){
        String ip = null;
        Enumeration allNetInterfaces;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                List<InterfaceAddress> InterfaceAddress = netInterface.getInterfaceAddresses();
                for (InterfaceAddress add : InterfaceAddress) {
                    InetAddress Ip = add.getAddress();
                    if (Ip != null && Ip instanceof Inet4Address) {
                        ip = Ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            log.warn("获取本机Ip失败:异常信息:"+e.getMessage());
        }
        return ip;
    }

    /**
     * 生成二维码图片 不存储 直接以流的形式输出到页面
     * @param content
     * @param response
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static JSONObject encodeQrcode(String content, HttpServletResponse response){
        ReturnResult returnResult = new ReturnResult();
        if(StringUtils.isBlank(content)){
            returnResult.setMessage("参数为空！");
            return ResultJSONUtils.getJSONObjectBean(returnResult);
        }
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Map hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); //设置字符集编码类型
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300,hints);
            BufferedImage image = toBufferedImage(bitMatrix);
            //输出二维码图片流
            try {
                ImageIO.write(image, "png", response.getOutputStream());
                returnResult.setStatus(Boolean.TRUE);
                returnResult.setMessage("二维码成功！");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (WriterException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return ResultJSONUtils.getJSONObjectBean(returnResult);
    }
//
//    /**
//     * 静态生成二维码 存储在磁盘上
//     * @param content  //二维码信息
//     * @param contextPath //上下文相对路径
//     * @param realPath    //磁盘真实路径
//     * @param subPath     //子路径
//     * @return
//     */
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    public static String generateQrcode(String content,String contextPath,String realPath,String subPath){
//        if(content==null || realPath==null)
//            return null;
//        String fileName = generateFileName(content.getBytes())+".png";
//        String url = "/" + UPLOAD + contextPath + "/" + subPath + "/" + fileName;//图片在项目中存储的相对路径
//        String filePath = url;
//        //如果是部署在服务器上的情况，则需要到webapps/下面的upload目录
//        if (StringUtils.isNotBlank(contextPath) || realPath.endsWith("root")) {
//            filePath = ".." + url;
//        }
//        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
//        Map hints = new HashMap();
//        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); //设置字符集编码类型
//        BitMatrix bitMatrix = null;
//        try {
//            bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300,hints);
//            File file1 = new File(realPath,filePath); //创建存储图片的文件
//            try {
//                writeToFile(bitMatrix, "png", file1); //存储二维码图片
//                return filePath;
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        } catch (WriterException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//        return null;
//    }
//    private static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
//        BufferedImage image = toBufferedImage(matrix);
//        if (!ImageIO.write(image, format, file)) {
//            throw new IOException("Could not write an image of format " + format + " to " + file);
//        }
//    }
//    private static BufferedImage toBufferedImage(BitMatrix matrix) {
//        int width = matrix.getWidth();
//        int height = matrix.getHeight();
//        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
//            }
//        }
//        return image;
//    }
//    private static String generateFileName(byte[] content) {
//        return CryptUtil.md5(content);  //md5加密
//    }

}