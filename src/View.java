import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;


public class View {
    JTextArea text;
    JTextArea code;
    String x;


    boolean showImage(String imageString, String outPutpath) throws IOException{

        if (imageString == null) {
            return false;
        }
        Base64.Decoder decoder = Base64.getDecoder();
            // 解密
            byte[] b = imageString.getBytes(StandardCharsets.UTF_8);
            b =        decoder.decode(b);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(outPutpath);
            out.write(b);
            out.flush();
            out.close();
            return true;
    }


    public void fileChooser(String mode) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Text File", "txt");
        //设置文件类型
        chooser.setFileFilter(filter);
        //打开选择器面板
        int returnVal = chooser.showSaveDialog(new JPanel());
        //保存文件从这里入手，输出的是文件名
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("你打开的文件夹是: " +
                    chooser.getSelectedFile().getPath());
            String path = chooser.getSelectedFile().getPath();
            try {
                File f = new File(path + ".txt");
                System.out.println(f.getAbsolutePath());
                f.createNewFile();
                FileOutputStream out = new FileOutputStream(f);
                if (mode.equals("Text")) {
                    out.write(text.getText().getBytes());
                } else {
                    out.write(code.getText().getBytes());
                }

                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getImageStr(String filePath) {
        InputStream inputStream = null;
        if (!filePath.endsWith(".png")) {
            filePath += ".png";
        }
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

    //    从制定位置读取文件内容
    public String readToString(String filePath) {
        File file = new File(filePath);
        Long filelength = file.length(); // 获取文件长度
        byte[] filecontent = new byte[filelength.intValue()];
        Boolean is_Txt = filePath.endsWith(".txt");
        String fileContentArr = "";
        try {
            if (is_Txt) {
                FileInputStream in = new FileInputStream(file);
                in.read(filecontent);
                in.close();
                fileContentArr = new String(filecontent);
            } else {
                fileContentArr = getImageStr(filePath);
                x = fileContentArr;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return fileContentArr; // 返回文件内容,默认编码
    }

    //    根据指定模式读取文件到位置
    public void uploadFromFile(String mode, String path) {

        String input = readToString(path);

        if (mode.equals("Text")) {
            text.setText(input);
        } else {
            code.setText(input);
        }
    }

    //    初始化加密/解密页面
    void placeComponents(JPanel panel) {

        panel.setLayout(null);

        JLabel textLabel = new JLabel("Plain Text:");
        textLabel.setBounds(10, 5, 80, 30);
        panel.add(textLabel);

        text = new JTextArea();
        text.setBounds(10, 40, 230, 180);
        panel.add(text);
        text.setToolTipText("Plain Text");

        JLabel codeLabel = new JLabel("Cipher:");
        codeLabel.setBounds(350, 5, 80, 30);
        panel.add(codeLabel);
        code = new JTextArea();
        code.setBounds(350, 40, 240, 180);
        panel.add(code);
        code.setToolTipText("Cipher");

        JLabel keyLabel = new JLabel("Key:");
        keyLabel.setBounds(275, 5, 80, 30);
        panel.add(keyLabel);
        JTextField key = new JTextField();
        key.setBounds(255, 35, 80, 30);
        panel.add(key);
        key.setToolTipText("Key");


        Model model = new Model();
        // 创建登录按钮
        JButton encode = new JButton("Encode>");
        encode.setBounds(240, 95, 75, 40);
//    监听输入框内容并返还给des模型处理
        encode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!key.getText().equals("")) {
                    if (!text.getText().equals("")) {
                        String cipher = "";
                        cipher = model.DES(text.getText(), key.getText(), "encrypt");
                        code.setText(cipher);
                    } else {
                        noTextWarn(panel);
                    }
                } else {
                    noKeyWarn(panel);
                }


            }
        });
        panel.add(encode);

        JButton decode = new JButton("<Decode");
        decode.setBounds(275, 150, 75, 40);
//    监听输入框内容并返还给des模型处理
        decode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!key.getText().equals("")) {
                    if (!code.getText().equals("")) {
                        String plainText = "";
                        try {
                            plainText = model.DES(code.getText(), key.getText(), "decrypt");
                        } catch (Exception exception) {
                            wrongCipherWarn(panel);
                        }
                        text.setText(plainText);
                    } else {
                        noCipherWarn(panel);
                    }
                } else {
                    noKeyWarn(panel);
                }
            }
        });
        panel.add(decode);

        JButton saveText = new JButton("Save Plain Text");
        saveText.setBounds(10, 320, 130, 40);

        saveText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser("Text");
            }
        });
        panel.add(saveText);

        JButton saveCipher = new JButton("Save Cipher Text");
        saveCipher.setBounds(460, 320, 130, 40);

        saveCipher.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser("Code");

            }
        });
        panel.add(saveCipher);

        JButton upLoadText = new JButton("Upload from file..");
        upLoadText.setBounds(0, 225, 130, 30);
// 从本地选取文件上传内容
        upLoadText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jfc.showDialog(new JLabel(), "选择");
                File file = jfc.getSelectedFile();
                uploadFromFile("Text", file.getAbsolutePath());
            }
        });
        panel.add(upLoadText);
        JButton upLoadCipher = new JButton("Upload from file..");
        upLoadCipher.setBounds(460, 225, 130, 30);
// 从本地选取文件上传内容
        upLoadCipher.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jfc.showDialog(new JLabel(), "选择");
                File file = jfc.getSelectedFile();
                uploadFromFile("Code", file.getAbsolutePath());
            }
        });
        panel.add(upLoadCipher);

        JButton decodeImage = new JButton("Decode Image");
        decodeImage.setBounds(330, 250, 130, 40);
// 从本地选取文件上传内容
        decodeImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!key.getText().equals("")) {
                    if (!code.getText().equals("")) {
                        String plainText = "";
                        try {
                            plainText = model.DES(code.getText(), key.getText(), "decrypt");
                        } catch (Exception exception) {
                            wrongCipherWarn(panel);
                        }
                        text.setText(plainText);
                        try {
                            showImage(text.getText(), "./result.png");
                        } catch (IOException ioException) {
                            wrongGraph(panel);
                        }
                    } else {
                        noCipherWarn(panel);
                    }
                } else {
                    noKeyWarn(panel);
                }

            }
        });
        panel.add(decodeImage);

    }

    static void noKeyWarn(JPanel panel) {
        JOptionPane.showMessageDialog(panel, "No key inputted! Please input cipher key!", "Warning!", JOptionPane.WARNING_MESSAGE);
    }

    static void noTextWarn(JPanel panel) {
        JOptionPane.showMessageDialog(panel, "No plain Text inputted! Please click Encode button after inputting", "Warning!", JOptionPane.WARNING_MESSAGE);
    }

    static void noCipherWarn(JPanel panel) {
        JOptionPane.showMessageDialog(panel, "No cipher inputted! Please click Decode button after inputting", "Warning!", JOptionPane.WARNING_MESSAGE);

    }

    static void wrongCipherWarn(JPanel panel) {
        JOptionPane.showMessageDialog(panel, "Wrong Cipher inputted! Please check the cipher!", "Warning!", JOptionPane.WARNING_MESSAGE);

    }
    static void wrongGraph(JPanel panel){
        JOptionPane.showMessageDialog(panel, "Wrong Graph Index! Please retry!", "Warning!", JOptionPane.WARNING_MESSAGE);
    }
    public static void main(String[] args) {
        // 创建 JFrame 实例
        JFrame frame = new JFrame("DES Encrypt");
        // Setting the width and height of frame
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        View view = new View();
        // 添加面板
        frame.add(panel);
        view.placeComponents(panel);
        frame.setVisible(true);
    }
}

