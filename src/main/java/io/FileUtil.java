package io;

import command.CommandUtil;
import config.Config;
import exception.AppException;
import logger.AppLogger;
import org.apache.commons.io.IOUtils;
import resource.MessageApi;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {
    private static final AppLogger logger = AppLogger.getLogger(FileUtil.class);

    static public void copy(String from, String to) throws Exception {
        Files.copy(new File(from).toPath(), new File(to).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    static public void copyFileToFolderIfExists(String fromFilePath, String toFolder) throws Exception {
        File fromFile = new File(fromFilePath);
        if (!fromFile.exists()) {
            return;
        }
        String toFilePath = toFolder + "/" + fromFile.getName();
        copy(fromFilePath, toFilePath);
    }

    static public void copyFolderIfExists(String from, String to) throws Exception {
        File fromFile = new File(from);
        if (!fromFile.exists()) {
            return;
        }
        copyRecursive(fromFile, new File(to));
    }

    // not using Files.copyFolder, as ity fails in case file is being modified while copying
    private static void copyRecursive(File from, File to) throws Exception {
        if (from.isFile()) {
            copy(from.getAbsolutePath(), to.getAbsolutePath());
            return;
        }
        File[] files = from.listFiles();
        if (files == null) {
            return;
        }
        mkdirs(to);
        for (File sub : files) {
            File subTarget = new File(to.getAbsoluteFile() + "/" + sub.getName());
            copyRecursive(sub, subTarget);
        }
    }

    // not using Files.readAllBytes to avoid special files reading (pipes, virtual files...)
    static public String getAllData(String path) throws Exception {
        if (!new File(path).exists()) {
            // we throw exception here since the error from Files class is ugly
            throw new AppException("file " + path + " does not exists");
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            byte buffer[] = new byte[1024];
            while (true) {
                int read = fileInputStream.read(buffer);
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(buffer, 0, read);
            }
        }
        return byteArrayOutputStream.toString(Encoding.ENCODING);
    }

    static public void saveToFile(String path, String data) throws Exception {
        checkOrCreateParentDir(path);
        try (PrintWriter out = new PrintWriter(path)) {
            out.print(data);
        }
    }

    public static void checkOrCreateParentDir(String path) throws Exception {
        File file = new File(path);
        File folder = file.getParentFile();
        if (folder == null) {
            return;
        }
        if (folder.exists()) {
            return;
        }
        mkdirs(folder);
    }

    static public String getTimeForFile() throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return simpleDateFormat.format(new Date());
    }

    public static String loadFileFromappRoot(String relativePath) throws Exception {
        String appHome = Config.getInstance().getRootPath();
        try (FileInputStream input = new FileInputStream(appHome + "/" + relativePath)) {
            return IOUtils.toString(input, Encoding.ENCODING);
        }
    }

    public static LineNumberReader getLineNumberReader(String file) throws Exception {
        FileReader fileReader = new FileReader(file);
        return new LineNumberReader(fileReader);
    }

    public static int deleteDirectoryContentsAndVerify(String directory) throws Exception {
        File[] files = new File(directory).listFiles();
        if (files == null) {
            return 0;
        }

        int numberOfDeleted = 0;
        for (File sub : files) {
            if (!sub.isDirectory()) {
                deleteAndVerify(sub.getPath());
                ++numberOfDeleted;
            }
        }
        return numberOfDeleted;
    }

    public static void deleteRecursive(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            deleteAndVerify(path);
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File sub : files) {
                deleteRecursive(sub.getPath());
            }
            deleteAndVerify(path);
        }
    }

    public static void deleteAndVerify(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (file.delete()) {
            return;
        }
        // unable to delete file
        throw MessageApi.getException("app00921",
                "PATH", path);
    }

    static public void chmod(String path, String mode, boolean isRecursive) throws Exception {
        String command = "sudo chmod " + mode + " " + path;
        if (isRecursive) {
            command += " -R";
        }
        CommandUtil.runCommandAndGetOutput(command);
    }

    static public void mkdirs(File file) throws Exception {
        if (!file.mkdirs()) {
            logger.debug("failed creating folder {}", file.getAbsoluteFile());
        }
    }

    static public void mkdirs(String path) throws Exception {
        mkdirs(new File(path));
    }
}
