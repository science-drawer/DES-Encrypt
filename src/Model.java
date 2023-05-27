import java.nio.charset.StandardCharsets;
import java.util.*;

public class Model {
    private Base64.Encoder b64encoder = Base64.getEncoder();
    private Base64.Decoder b64decoder = Base64.getDecoder();

    //整数转二进制数组，指定位长 n，大端序
    private int[] int2bin(int a, int n) {
        int[] res = new int[n];
        Arrays.fill(res, 0);

        for (int i = 0; i < n; i++) {
            res[n - i - 1] = a % 2;
            a = a / 2;
        }

        return res;
    }

    //二进制数组转整数
    private int bin2int(int[] a) {
        int res = 0;
        for (int i = 0; i < a.length; i++) {
            res += Math.pow(2, a.length - i - 1) * a[i];
        }
        return res;
    }

    //字符串转二进制数组
    private int[] str2bin(String message, String method) {
        byte[] mes_bytes;
        //用utf - 8 编码
        if (method.equals("decrypt")) {
            mes_bytes = b64decoder.decode(message.getBytes(StandardCharsets.UTF_8));
        } else {
            mes_bytes = message.getBytes(StandardCharsets.UTF_8);
        }

        List<Integer> list = new ArrayList<Integer>();
        for (int i : mes_bytes) {
            //补码
            if (i < 0) {
                i += 256;
            }
            for (int k : int2bin(i, 8)) {
                list.add(k);
            }
        }
        if (list.size() % 64 != 0) {
            int t = list.size();
            for (int i = 0; i < 64 - (t % 64); i++) {
                list.add(0);
            }
        }
        int[] res = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            res[i] = list.get(i);
        }
        return res;
    }

    private String bin2str(int[] lst, String method) {
        String res = "";
        ArrayList<Byte> res_bytes_list = new ArrayList();

        //8个为一组，把Array转化为byte
        for (int i = 0; i < lst.length; i += 8) {
            int[] tmp_lst = new int[8];
            //填充
            Arrays.fill(tmp_lst, 0);
            for (int j = i; j < Math.min(i + 8, lst.length); j++) {
                tmp_lst[j - i] = lst[j];
            }

            //数字转二进制
            int x = bin2int(tmp_lst);
            res_bytes_list.add((byte) ((x & 0xFF)));
        }

        //list转Array
        byte[] res_bytes = new byte[res_bytes_list.size()];
        for (int i = 0; i < res_bytes_list.size(); i++) {
            res_bytes[i] = res_bytes_list.get(i);
        }

        //编解码
        if (method.equals("decrypt")) {
            res = new String(res_bytes, StandardCharsets.UTF_8);
        } else {
            res = new String(b64encoder.encode(res_bytes), StandardCharsets.UTF_8);
        }
        return res;
    }

    private int[] leftRotate(int[] a, int off) {
        int[] t = new int[a.length];
        for (int i = off; i < a.length; i++) {
            t[i - off] = a[i];
        }

        for (int i = 0; i < off; i++) {
            t[a.length - i - 1] = a[off - i - 1];
        }

        return t;
    }

    private int[] binXor(int[] a, int[] b) {
        int[] res = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            if (a[i] == b[i]) {
                res[i] = 0;
            } else {
                res[i] = 1;
            }
        }

        return res;
    }

    //初始置换 没测试
    private int[] IP(int[] a) {
        int[] ip = {58, 50, 42, 34, 26, 18, 10, 2,
                60, 52, 44, 36, 28, 20, 12, 4,
                62, 54, 46, 38, 30, 22, 14, 6,
                64, 56, 48, 40, 32, 24, 16, 8,
                57, 49, 41, 33, 25, 17, 9, 1,
                59, 51, 43, 35, 27, 19, 11, 3,
                61, 53, 45, 37, 29, 21, 13, 5,
                63, 55, 47, 39, 31, 23, 15, 7};
        int[] res = new int[ip.length];
        for (int i = 0; i < ip.length; i++) {
            res[i] = a[ip[i] - 1];
        }

        return res;
    }

    //最终置换
    private int[] FP(int[] a) {
        int[] ip = {40, 8, 48, 16, 56, 24, 64, 32,
                39, 7, 47, 15, 55, 23, 63, 31,
                38, 6, 46, 14, 54, 22, 62, 30,
                37, 5, 45, 13, 53, 21, 61, 29,
                36, 4, 44, 12, 52, 20, 60, 28,
                35, 3, 43, 11, 51, 19, 59, 27,
                34, 2, 42, 10, 50, 18, 58, 26,
                33, 1, 41, 9, 49, 17, 57, 25};
        int[] res = new int[ip.length];
        for (int i = 0; i < ip.length; i++) {
            res[i] = a[ip[i] - 1];
        }

        return res;
    }

    private int[][] PC1(int[] key) {
        int[][] res = new int[2][];
        int[] pc1_l = {57, 49, 41, 33, 25, 17, 9,
                1, 58, 50, 42, 34, 26, 18,
                10, 2, 59, 51, 43, 35, 27,
                19, 11, 3, 60, 52, 44, 36};
        int[] pc1_r = {63, 55, 47, 39, 31, 23, 15,
                7, 62, 54, 46, 38, 30, 22,
                14, 6, 61, 53, 45, 37, 29,
                21, 13, 5, 28, 20, 12, 4};
        int[] l = new int[pc1_l.length];
        int[] r = new int[pc1_r.length];
        for (int i = 0; i < l.length; i++) {
            l[i] = key[pc1_l[i] - 1];
        }
        for (int i = 0; i < r.length; i++) {
            r[i] = key[pc1_r[i] - 1];
        }
        res[0] = l;
        res[1] = r;
        return res;
    }

    //选择置换2
    //从56位的密钥中选取48位子密钥
    private int[] PC2(int[] a) {
        int[] ip = {14, 17, 11, 24, 1, 5,
                3, 28, 15, 6, 21, 10,
                23, 19, 12, 4, 26, 8,
                16, 7, 27, 20, 13, 2,
                41, 52, 31, 37, 47, 55,
                30, 40, 51, 45, 33, 48,
                44, 49, 39, 56, 34, 53,
                46, 42, 50, 36, 29, 32};
        int[] res = new int[ip.length];
        for (int i = 0; i < ip.length; i++) {
            res[i] = a[ip[i] - 1];
        }

        return res;
    }

    //子密钥生成算法，由一个64位主密钥导出16个48位子密钥
    private int[][] keyGen(int[] key) {
        int[] t = new int[64];
        System.arraycopy(key, 0, t, 0, 64);

        int[][] lr = PC1(t);
        int[] off = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
        int[][] res = new int[16][];

        for (int i = 0; i < 16; i++) {
            lr[0] = leftRotate(lr[0], off[i]);
            lr[1] = leftRotate(lr[1], off[i]);

            int[] x = new int[56];
            for (int j = 0; j < x.length; j++) {
                if (j < 28) {
                    x[j] = lr[0][j];
                } else {
                    x[j] = lr[1][j - 28];
                }
            }

            res[i] = PC2(x);
        }
        return res;
    }

    //S盒变换，输入48位，输出32位
    private int[] S(int[] a) {
        int[][] S_box = {{14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
                0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
                4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
                15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13},
                {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
                        3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
                        0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
                        13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9},
                {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
                        13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
                        13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
                        1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12},
                {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
                        13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
                        10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
                        3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14},
                {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
                        14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
                        4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
                        11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3},
                {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
                        10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
                        9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
                        4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13},
                {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
                        13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
                        1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
                        6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12},
                {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
                        1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
                        7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
                        2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}};

        int[] res = new int[32];
        for (int i = 0; i < 8; i++) {
            int n = i * 6;
            int[] t = {a[n], a[n + 5], a[n + 1], a[n + 2], a[n + 3], a[n + 4]};
            int r = S_box[i][bin2int(t)];
            int[] tmp = int2bin(r, 4);
            for (int j = 0; j < 4; j++) {
                res[i * 4 + j] = tmp[j];
            }
        }

        return res;
    }

    //扩张置换，将32位的半块扩展到48位
    private int[] expand(int[] a) {
        int[] ip = {32, 1, 2, 3, 4, 5,
                4, 5, 6, 7, 8, 9,
                8, 9, 10, 11, 12, 13,
                12, 13, 14, 15, 16, 17,
                16, 17, 18, 19, 20, 21,
                20, 21, 22, 23, 24, 25,
                24, 25, 26, 27, 28, 29,
                28, 29, 30, 31, 32, 1};
        int[] res = new int[ip.length];
        for (int i = 0; i < ip.length; i++) {
            res[i] = a[ip[i] - 1];
        }

        return res;
    }

    private int[] P(int[] a) {
        int[] ip = {16, 7, 20, 21,
                29, 12, 28, 17,
                1, 15, 23, 26,
                5, 18, 31, 10,
                2, 8, 24, 14,
                32, 27, 3, 9,
                19, 13, 30, 6,
                22, 11, 4, 25};
        int[] res = new int[ip.length];
        for (int i = 0; i < ip.length; i++) {
            res[i] = a[ip[i] - 1];
        }
        return res;
    }

    // F函数，用于处理一个半块
    private int[] Feistel(int[] array, int[] subKey) {
        int[] temple = binXor(expand(array), subKey);
        temple = S(temple);
        temple = P(temple);
        return temple;
    }

    private int[][] cipherRound(int[] left, int[] right, int[] subKey) {
        int[][] temple = new int[2][];
        temple[0] = right;
        temple[1] = binXor(left, Feistel(right, subKey));
        return temple;
    }

    private int[][] keySchedule(String key, String method){
        //生成钥匙的时候使用utf-8编码，写"encrypt"为了简化复用代码
        int[][] subkeys = keyGen(str2bin(key, "encrypt"));

        // 解码时逆转key set
        if (method.equals("decrypt")) {
            int[][] t = new int[16][];
            for (int i = 0; i < 16; i++) {
                t[i] = subkeys[16 - i - 1];
            }
            subkeys = t;
        }
        return subkeys;
    }
    private int[] getArray(int[] originalArray, int start, int length){
        int[] block = new int[length];
        for (int j = 0; j < length; j++) {
            block[j] = originalArray[start + j];
        }
        return block;
    }
    public String DES(String plain, String key, String method) {
        int[][] subkeys = keySchedule(key, method);

        // 分割总list，分为长度为64的小块
        int[] text_blocks = str2bin(plain, method);

        int[][] code_blocks = new int[Math.max(1, text_blocks.length / 64)][];
        for (int i = 0; i < text_blocks.length; i += 64) {
            int[] block = getArray(text_blocks, i, 64);

            int[] ini_block = IP(block);

            int[] left = new int[32];
            int[] right = new int[32];
            for (int j = 0; j < 64; j++) {
                if (j < 32) {
                    left[j] = ini_block[j];
                } else {
                    right[j - 32] = ini_block[j];
                }
            }

            //16 轮次的加密
            int[][] lr = {left, right};
            for (int j = 0; j < 16; j++) {
                lr = cipherRound(lr[0], lr[1], subkeys[j]);
            }

            //合并左右俩矩阵
            int[] fin = new int[64];
            for (int j = 0; j < 64; j++) {
                if (j < 32) {
                    fin[j] = lr[1][j];
                } else {
                    fin[j] = lr[0][j - 32];
                }
            }

            code_blocks[i / 64] = (FP(fin));
        }

        int[] code_blocks_bits = new int[text_blocks.length];
        int n = 0;
        for (int i = 0; i < code_blocks.length; i++) {
            for (int j = 0; j < code_blocks[i].length; j++) {
                code_blocks_bits[n] = code_blocks[i][j];
                n++;
            }
        }
        return bin2str(code_blocks_bits, method);
    }

}
