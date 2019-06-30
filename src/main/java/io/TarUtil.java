package io;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;
import resource.MessageApi;

import java.io.*;

public class TarUtil {

    public static void extractTarGz(String tarGzPath, String targetFolder) throws Exception {
        if (!new File(tarGzPath).exists()) {
            //file is not found
            throw MessageApi.getException("app00908",
                    "PATH", tarGzPath);
        }
        new File(targetFolder).mkdirs();
        FileInputStream fileInputStream = new FileInputStream(tarGzPath);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        GzipCompressorInputStream compressorInputStream = new GzipCompressorInputStream(bufferedInputStream);

        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(compressorInputStream)) {
            extractTarGzWorker(tarIn, targetFolder);
        }
    }

    private static void extractTarGzWorker(TarArchiveInputStream tarIn, String targetFolder) throws Exception {
        while (true) {
            TarArchiveEntry entry = (TarArchiveEntry) tarIn.getNextEntry();
            if (entry == null) {
                break;
            }

            if (entry.isDirectory()) {
                File f = new File(targetFolder + "/" + entry.getName());
                f.mkdirs();
                continue;
            }

            extractSingleFile(tarIn, targetFolder, entry);
        }
    }

    private static void extractSingleFile(TarArchiveInputStream tarIn, String targetFolder, TarArchiveEntry entry) throws IOException {
        byte data[] = new byte[1024];

        String path = targetFolder + "/" + entry.getName();
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(path))) {
            while (true) {
                int count = tarIn.read(data, 0, data.length);
                if (count == -1) {
                    break;
                }
                bufferedOutputStream.write(data, 0, count);
            }
        }
    }

    public static void createTarGz(String tarGzPath, String compressPath) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(tarGzPath));
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        GzipCompressorOutputStream compressorOutputStream = new GzipCompressorOutputStream(bufferedOutputStream);
        TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(compressorOutputStream);
        try {
            File file = new File(compressPath);
            if (file.isFile()) {
                addToTarGz(tarArchiveOutputStream, compressPath, null);
            } else {
                File[] children = file.listFiles();
                if (children == null) {
                    return;
                }
                for (File child : children) {
                    addToTarGz(tarArchiveOutputStream, child.getAbsolutePath(), "");
                }
            }
        } finally {
            tarArchiveOutputStream.finish();
            tarArchiveOutputStream.close();
        }
    }

    private static void addToTarGz(TarArchiveOutputStream tarArchiveOutputStream, String compressPath, String base) throws Exception {
        File file = new File(compressPath);
        String entryName;
        if (base == null) {
            entryName = file.getName();
        } else {
            entryName = base + "/" + file.getName();
        }
        TarArchiveEntry tarEntry = new TarArchiveEntry(file, entryName);
        tarArchiveOutputStream.putArchiveEntry(tarEntry);

        if (file.isFile()) {
            try (FileInputStream input = new FileInputStream(file)) {
                IOUtils.copy(input, tarArchiveOutputStream);
            }
            tarArchiveOutputStream.closeArchiveEntry();
            return;
        }

        tarArchiveOutputStream.closeArchiveEntry();
        File[] children = file.listFiles();
        if (children == null) {
            return;
        }
        for (File child : children) {
            addToTarGz(tarArchiveOutputStream, child.getAbsolutePath(), entryName);
        }
    }

}
