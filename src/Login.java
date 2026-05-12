import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Login {
    //取消了main函数，改成showLoginWindow方法，以便在Main类中调用
    public static JFrame createLoginWindow() {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.setSize(400, 220);
        loginFrame.setLayout(null);
        loginFrame.setLocationRelativeTo(null);

        //添加账号和密码的标签
        JLabel usernameLabel = new JLabel("Account:");
        usernameLabel.setSize(80, 30);
        usernameLabel.setLocation(50, 40);

        //输入账号的文本框
        JTextField username = new JTextField();
        username.setSize(160, 30);
        username.setLocation(130, 40);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setSize(80, 30);
        passwordLabel.setLocation(50, 80);

        //输入密码的文本框，输入内容会被隐藏
        JPasswordField password = new JPasswordField();
        password.setSize(160, 30);
        password.setLocation(130, 80);

        JButton loginButton = new JButton("Login");
        loginButton.setSize(100, 30);
        loginButton.setLocation(130, 130);

        loginButton.addActionListener(e -> {
            String thisUser = username.getText();
            //getPassword返回char[]，我们要把它转换到String
            String thisPassword = new String(password.getPassword());
            boolean match = false;

            try {
                //从Users.txt中读取已经注册的账号和密码
                Scanner in = new Scanner(new File("src/Users.txt"));
                while (in.hasNext()) {
                    String currentUsername = in.next();
                    String currentPassword = in.next();

                    //如果账号和密码都相同，就说明登录成功
                    if (currentUsername.equals(thisUser)
                            && currentPassword.equals(thisPassword)) {
                        match = true;
                        break;
                    }
                }
                in.close();
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(loginFrame, "User file not found.");
                return;
            }

            if (match) {
                JOptionPane.showMessageDialog(loginFrame, "Welcome!");
                //如果用户成功进入游戏系统那接下来就是选择困难/简单mode
                Boolean hardMode = DifficultySelector.chooseDifficulty(loginFrame);
                //如果是null(也就是取消或者退出)，直接返回登陆界面
                if (hardMode == null) return;
                loginFrame.dispose();//关闭并释放这个窗口占用的资源
                WindowManager.switchTo(new GameFrame(hardMode));
            } else JOptionPane.showMessageDialog(loginFrame, "Wrong account or password.");
        });

        loginFrame.add(usernameLabel);
        loginFrame.add(username);
        loginFrame.add(passwordLabel);
        loginFrame.add(password);
        loginFrame.add(loginButton);

        return loginFrame;
    }
}
