package com.music.util;

public class FileUtils {

  public static String getExtension(String fileName) {
    if (fileName == null) {
      return "";
    }
    int lastDotIndex = fileName.lastIndexOf('.');
    return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1).toLowerCase() : "";
  }
}
