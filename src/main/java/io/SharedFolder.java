package io;

import java.io.File;

public class SharedFolder {
    static public File getFolder() throws Exception {
        File root = new File("/mnt/filepipe/app");
        if (!root.exists()) {
            root = new File("data/app");
        }

        return root;
    }

    static public String getPathForSharedFile(String configuredPath, String subFolderName,
                                              String fileNamePrefix, String fileNameSuffix) throws Exception {
        if (configuredPath != null) {
            return configuredPath;
        }
        File root = SharedFolder.getFolder();
        File subFolder = new File(root.getAbsoluteFile() + "/" + subFolderName);
        FileUtil.mkdirs(subFolder);
        return subFolder.getAbsolutePath() + "/" + fileNamePrefix + "_" + FileUtil.getTimeForFile() + fileNameSuffix;
    }
}
