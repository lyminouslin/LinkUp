package ui;

import storage.UserStorage;
import util.Encryptor;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;

public class Register {
    public static JFrame createRegisterWindow() {
        //创建register的框架
        JFrame registerFrame = new JFrame("Register");
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.setSize(400, 240);
        registerFrame.setLayout(null);
        registerFrame.setLocationRelativeTo(null);

        //添加账号和密码的标签
        JLabel usernameLabel = new JLabel("Account:");
        usernameLabel.setSize(80, 30);
        usernameLabel.setLocation(50, 40);

        //输入新账号的文本框
        JTextField username = new JTextField();
        username.setSize(160, 30);
        username.setLocation(130, 40);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setSize(80, 30);
        passwordLabel.setLocation(50, 80);

        //输入新密码的文本框，因为是passwordField所以输入内容会被隐藏
        JPasswordField password = new JPasswordField();
        password.setSize(160, 30);
        password.setLocation(130, 80);
        //注册按钮
        JButton registerButton = new JButton("Register");
        registerButton.setSize(100, 30);
        registerButton.setLocation(130, 130);

        //注册按钮的监听器，点击后会把新账号和新密码写入Users.txt
        registerButton.addActionListener(e -> {
            String newUser = username.getText();
            String newPassword = new String(password.getPassword());
            //需要先检查账密是否合法
            if (newUser.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(registerFrame, "Account or password cannot be empty.");
                return;
            }

            Map<String, String> users = UserStorage.loadUsers();

            if (users.containsKey(newUser)) {
                JOptionPane.showMessageDialog(registerFrame, "Account already exists.");
                return;
            }

            String encryptedPassword = Encryptor.encrypt(newPassword, true);

            try {
                UserStorage.addUser(newUser, encryptedPassword);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(registerFrame, "Registration failed.");
                throw new RuntimeException(ex);
            }

            JOptionPane.showMessageDialog(registerFrame, "Register success!");
            registerFrame.dispose();
            WindowManager.showMainWindow();
        });

        registerFrame.add(usernameLabel);
        registerFrame.add(username);
        registerFrame.add(passwordLabel);
        registerFrame.add(password);
        registerFrame.add(registerButton);
        return registerFrame;
    }
}
