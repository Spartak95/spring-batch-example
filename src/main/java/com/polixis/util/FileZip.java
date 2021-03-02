package com.polixis.util;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class FileZip {
    public static void main(String[] args) throws ZipException {
        String fileZip = "src/main/resources/data.zip";
        String destDir = "src/main/resources/data";

        ZipFile zipFile = new ZipFile(fileZip);
        zipFile.extractAll(destDir);
    }
}

