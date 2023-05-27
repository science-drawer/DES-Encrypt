import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

class FileChooser extends JFrame implements ActionListener {
    JButton open=null;
    public static void main(String[] args) {
        new FileChooser();
    }
    public FileChooser(){
        open=new JButton("打开文件选择器");
        this.add(open);
        this.setBounds(400, 200, 100, 100);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        open.addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        JFileChooser jfc=new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY );
        jfc.showDialog(new JLabel(), "选择");
        File file=jfc.getSelectedFile();
        System.out.println("文件:"+file.getAbsolutePath());

        System.out.println(jfc.getSelectedFile().getName());

    }

}