package com.stefanie20.ReadDay;

import java.io.File;
import java.util.Scanner;

/**
 * Created by F317 on 16/2/21.
 */
public class Test1 {
    public static void main(String[] args) throws Exception{
        File file = new File("UserInfo.dat");
        System.out.println(file.exists());
        System.out.println(file.isFile());
        Scanner scanner = new Scanner(file);
        scanner.nextLine();
        scanner.nextLine();
        String authString = scanner.nextLine();
        authString = authString.substring(5);
        System.out.println(authString);
    }


}
