import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Photo {
    /**
     * 字符串转图片
     *
     * @param imgStr   --->图片字符串
     * @param filename --->图片名
     * @return
     */
    public static boolean generateImage(String imgStr, String filename) {

        if (imgStr == null) {
            return false;
        }
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            // 解密
            byte[] b = decoder.decode(imgStr.getBytes(StandardCharsets.UTF_8));
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(filename);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;

    }

    /**
     * 图片转字符串
     *
     * @param filePath --->文件路径
     * @return
     */
    public static String getImageStr(String filePath) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(filePath);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 加密
        Base64.Encoder encoder = Base64.getEncoder();
        return new String(encoder.encode(data), StandardCharsets.UTF_8);
    }

    /*
     * 测试代码
     */
    public static void main(String[] args) {
        String imageStr = getImageStr("1.png");
        System.out.println(imageStr);
        boolean generateImage = generateImage(imageStr, "result.png");
        System.out.println(generateImage);
        String relativelyPath=System.getProperty("user.dir");
        System.out.println(relativelyPath);
//        File testFile = new File("2.png");
//        testFile.mkdir();
    }
}
