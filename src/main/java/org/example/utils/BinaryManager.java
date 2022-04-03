package org.example.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BinaryManager {
    public static List<Boolean> getBytes(String filepath) throws IOException {
        String strResult = new String(Files.readAllBytes(Paths.get(filepath)));
        List<Boolean> result = new ArrayList<>();

        strResult = strResult.replaceAll("\n", "");
        strResult = strResult.replaceAll(" ", "");

        for (int i = 0; i < 100_000_0; ++i) {
            if (strResult.charAt(i) == '1')
                result.add(true);
            if (strResult.charAt(i) == '0')
                result.add(false);
        }
        return result;
    }
}
