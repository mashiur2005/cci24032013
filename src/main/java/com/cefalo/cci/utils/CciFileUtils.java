package com.cefalo.cci.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CciFileUtils {
    public List<String> getAllFileNamesInDirectory(String dirPath) {
        File dir = new File(dirPath);

        try {
            System.out.println("Getting all files in " + dir.getCanonicalPath() + " including those in subdirectories");
            List<File> files = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            for (File file : files) {
                System.out.println("file: " + file.getCanonicalPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
