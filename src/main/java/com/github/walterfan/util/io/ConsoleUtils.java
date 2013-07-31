package com.github.walterfan.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleUtils {
        private static InputStreamReader inputStream = new InputStreamReader(System.in);
        private static BufferedReader reader = new BufferedReader(inputStream);

        public int getNumberFromConsole() {
            int number = 0;
            try {
                number = Integer.parseInt(reader.readLine());
            } catch (Exception e) {
                System.out.println("Enter a valid integer!!");
            }
            return number;
        }

        public String getStringFromConsole() {
            String strInput="";
            try {
                strInput = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return strInput;
        }

}
