package com.cefalo.cci.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;

public class Utils {

    public static Map<String, List<String>> ORGANIZATION_DETAILS = new HashMap<String, List<String>>();
    public static String HOME_DIR = System.getProperty("user.home");
    public static String FILE_SEPARATOR = System.getProperty("file.separator");
    public static String FILE_BASE_DIR= "epubs";
    public static String FILE_BASE_PATH = HOME_DIR + FILE_SEPARATOR + FILE_BASE_DIR;

    static {
         ORGANIZATION_DETAILS.put("Polaris", Arrays.asList("Addressa", "Harstadtidende"));
         ORGANIZATION_DETAILS.put("NHST", Arrays.asList("NHST-SPORTS", "NHST-NEWS"));
         ORGANIZATION_DETAILS.put("AxelSpringer", Arrays.asList("AxelSpringer-SPORTS", "AxelSpringer-ENTERTAINMENT"));
    }

    public static boolean isBlank(final String str) {
        if (Strings.isNullOrEmpty(str)) {
            return true;
        }

        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static String createETagHeaderValue(final long value) {
        return String.format("\"%s\"", value);
    }
}
