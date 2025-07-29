package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // 获取用户桌面路径
        String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
        // 拼接文件完整路径
        File file = new File(desktopPath, "test_one.txt");

        // 检查文件是否存在
        if (!file.exists()) {
            System.out.println("文件不存在: " + file.getAbsolutePath());
            return;
        }

        // 检查是否是文件
        if (!file.isFile()) {
            System.out.println("指定路径不是一个文件: " + file.getAbsolutePath());
            return;
        }

        // 读取文件内容并打印
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            System.out.println("文件内容如下:");
            System.out.println("-------------------");
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("-------------------");
        } catch (IOException e) {
            System.out.println("读取文件时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
    