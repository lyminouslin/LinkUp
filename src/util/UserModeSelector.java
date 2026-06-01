package util;

import javax.swing.*;
import java.awt.*;

public class UserModeSelector {
    
    //constructor，禁止public访问，因为这个类不需要任何参数来创建
    private UserModeSelector() {}

    public static Integer chooseUserAction(Component parent) {
        String[] options = {"登录", "注册", "取消"};
        int result = JOptionPane.showOptionDialog(//JOptionPane.showOptionDialog是swing当中的弹窗工具
                parent,
                "请选择操作",//提示文字
                "注册用户模式",//标题
                JOptionPane.DEFAULT_OPTION,//默认排序方式
                JOptionPane.QUESTION_MESSAGE,
                null,//不自定义图标
                options,//登录，注册取消三个选项
                options[0]//默认登陆
        );

        //根据用户按哪个按钮就选哪个按钮，0登录，1注册，取消返回是null
        if (result == 0) {
            return 0;
        }
        else if (result == 1) {
            return 1;
        }
        return null;
    }
}
