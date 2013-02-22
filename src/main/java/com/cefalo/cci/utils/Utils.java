package com.cefalo.cci.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    public static Map<String, List<String>> ORGANIZATION_DETAILS = new HashMap<String, List<String>>();
    public static String HOME_DIR = System.getProperty("user.home");

    static {
         ORGANIZATION_DETAILS.put("Polaris", Arrays.asList("Addressa", "Harstadtidende"));
         ORGANIZATION_DETAILS.put("NHST", Arrays.asList("NHST-SPORTS", "NHST-NEWS"));
         ORGANIZATION_DETAILS.put("AxelSpringer", Arrays.asList("AxelSpringer-SPORTS", "AxelSpringer-ENTERTAINMENT"));
    }

}
