package com.cefalo.cci.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {
    private final Logger logger = LoggerFactory.getLogger(Utils.class.getName());

    public static Map<String, List<String>> ORGANIZATION_DETAILS = new HashMap<String, List<String>>();
    public static String HOME_DIR = "E:/test";
    public static String FILE_SEPARATOR = System.getProperty("file.separator");
    public static int CLEANING_INTERVAL = 30;
    public static String CACHE_DIR_NAME = "CachedStorage";
    public static String CACHED_EPUBS_DIR_NAME = "EPUBS";
    public static String TMP_DIR_NAME = "tmp";
    public static String CACHE_DIR_FULLPATH = HOME_DIR + FILE_SEPARATOR + CACHE_DIR_NAME + FILE_SEPARATOR;
    public static String CACHED_EPUBS_FULLPATH = CACHE_DIR_FULLPATH + CACHED_EPUBS_DIR_NAME + FILE_SEPARATOR;
    public static String TMP_DIR_FULLPATH = CACHE_DIR_FULLPATH + TMP_DIR_NAME + FILE_SEPARATOR;
    public static String DEFAULT_TZ = "GMT";
    public static String DATE_FORMAT = "yyyy-MM-dd";
    public static  Map<Integer, String> FILE_STATUS_MAP = new HashMap<>(3);

    static {
         ORGANIZATION_DETAILS.put("Polaris", Arrays.asList("Addressa", "Harstadtidende"));
         ORGANIZATION_DETAILS.put("NHST", Arrays.asList("NHST-SPORTS", "NHST-NEWS"));
         ORGANIZATION_DETAILS.put("AxelSpringer", Arrays.asList("AxelSpringer-SPORTS", "AxelSpringer-ENTERTAINMENT"));
    }

    static {
        FILE_STATUS_MAP.put(1, "Updated");
        FILE_STATUS_MAP.put(2, "Inserted");
        FILE_STATUS_MAP.put(3, "Deleted");
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

    public static void writeZipFileToDir(final InputStream inputStream, final String fileAbsolutePath) throws IOException {

        FileOutputStream tmpFileOutputStream = null;
        try {
            File tmpFile = new File(fileAbsolutePath);
            Files.createParentDirs(tmpFile);
            tmpFileOutputStream = new FileOutputStream(tmpFile);
            ByteStreams.copy(inputStream, tmpFileOutputStream);
        } catch (IOException io) {
            throw io;
        } finally {
            Closeables.close(tmpFileOutputStream, true);
        }
    }

    public static InputStream readFileFromDir(String fileAbsolutePath) throws IOException {
        FileInputStream tmpFileInputStream = null;

        try {
            File tmpFile = new File(fileAbsolutePath);
            tmpFileInputStream = new FileInputStream(tmpFile);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            throw fnfe;
        }
        return tmpFileInputStream;
    }

    public void deleteRecursive(File path){
        File[] c = path.listFiles();
        logger.info(String.format("Cleaning out folder: %s", path.toString()));

        if (c != null) {
            for (File file : c){
                if (file.isDirectory()){
                    logger.info(String.format("Deleting file: %s", file.toString()));
                    deleteRecursive(file);
                    file.delete();
                } else {
                    file.delete();
                }
            }
        }

        path.delete();
    }

    public static Date convertDateWithTZ(Date date, String tz) {
        DateTime dt = new DateTime(date, DateTimeZone.forID(tz));
        return dt.toDate();
    }

    public static Date convertDateWithTZ(Date date) {
        return convertDateWithTZ(date, DEFAULT_TZ);
    }

    public static Date convertDateFormatTZ(String dateStr) throws IllegalArgumentException {
        return convertDateFormatTZ(dateStr, DATE_FORMAT);
    }

    public static Date convertDateFormatTZ(String dateStr, String format) throws IllegalArgumentException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
        DateTime dateTime = formatter.parseDateTime(dateStr);
        return convertDateWithTZ(dateTime.toDate());
    }


    public static boolean areSetsEquals(Set<String> from, Set<String> to) {
          return Sets.difference(from, to).size() == 0;
    }

    public static String getExtractedContent(String fileLoc, String fileName) throws IOException {
        ZipInputStream zipInputStream = null;
        try {
            zipInputStream =  new ZipInputStream(new FileInputStream(fileLoc));
            if (zipInputStream == null) {
                throw new IOException();
            }
             ZipEntry ze;
            byte[] bytes = null;
            while ((ze = zipInputStream.getNextEntry()) != null) {
                  if (fileName.equals(ze.getName())) {
                      bytes = ByteStreams.toByteArray(zipInputStream);
                      break;
                  }
            }
            if (bytes != null) {
                return new String(bytes);
            }
            return "";
        } finally {
            Closeables.close(zipInputStream, true);
        }
    }
}
