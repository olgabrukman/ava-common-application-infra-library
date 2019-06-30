package io;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    static public void unzip(String zipFile, String outputFolder) throws Exception {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            while (true) {
                ZipEntry zipEntry = zipInputStream.getNextEntry();
                if (zipEntry == null) {
                    break;
                }

                String fileName = zipEntry.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                if (zipEntry.isDirectory()) {
                    FileUtil.mkdirs(newFile);
                    continue;
                }
                FileUtil.mkdirs(newFile.getParent());

                try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                    copyStream(zipInputStream, fileOutputStream);
                }
                zipInputStream.closeEntry();
            }
        }
    }

    static public String list(String zipFile) throws Exception {
        StringBuilder result = new StringBuilder();
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            while (true) {
                ZipEntry zipEntry = zipInputStream.getNextEntry();
                if (zipEntry == null) {
                    break;
                }

                String fileName = zipEntry.getName();
                result.append(fileName);
                result.append("\n");
            }

            zipInputStream.close();
            return result.toString();
        }
    }

    private static void copyStream(InputStream inputStream, OutputStream outputStream) throws Exception {
        byte[] buffer = new byte[10 * 1024];
        while (true) {
            int len = inputStream.read(buffer);
            if (len == -1) {
                break;
            }
            outputStream.write(buffer, 0, len);
        }
    }


    static public void zip(String zipFile, String inputFolder) throws Exception {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            File file = new File(inputFolder);
            File subFiles[] = file.listFiles();
            if (subFiles != null) {
                for (File subFile : subFiles) {
                    addFileToZip(zipOutputStream, subFile, "");
                }
            }
        }
    }

    static private void addFileToZip(ZipOutputStream zipOutputStream, File file, String folderInZip) throws Exception {
        if (file.isFile()) {
            String pathInZip;
            if (folderInZip.length() == 0) {
                pathInZip = file.getName();
            } else {
                pathInZip = folderInZip + "/" + file.getName();
            }
            ZipEntry zipEntry = new ZipEntry(pathInZip);
            zipOutputStream.putNextEntry(zipEntry);
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                copyStream(fileInputStream, zipOutputStream);
            }
            zipOutputStream.closeEntry();
            return;
        }
        File subFiles[] = file.listFiles();
        if (subFiles == null) {
            return;
        }
        if (folderInZip.length() == 0) {
            folderInZip = file.getName();
        } else {
            folderInZip = folderInZip + "/" + file.getName();
        }
        for (File subFile : subFiles) {
            addFileToZip(zipOutputStream, subFile, folderInZip);
        }
    }


}
