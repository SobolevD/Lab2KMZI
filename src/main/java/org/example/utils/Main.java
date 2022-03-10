package org.example.utils;

public class Main {
    public static void main(String[] args) {
        McLarenMarsagliGenerator generator = new McLarenMarsagliGenerator(10, 5);

        for (int i = 0; i < 5; ++i) {
            System.out.println(generator.nextInt());
        }
    }
}
