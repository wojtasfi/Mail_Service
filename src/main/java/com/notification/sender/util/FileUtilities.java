package com.notification.sender.util;

import com.notification.sender.api.FilePath;
import com.notification.sender.api.FileString;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Component
public class FileUtilities {

    public File retreiveFileFromBase64String(FileString fileString) throws IOException {
        File file = new File(fileString.getFileName());
        byte[] data = Base64.decodeBase64(fileString.getFileString());
        FileUtils.writeByteArrayToFile(file, data);

        return file;
    }

    public File retreiveFileFromUrl(FilePath filePath) throws IOException {

        File file = new File(filePath.getFileName());
        URL url = new URL(filePath.getFilePath());

        FileUtils.copyURLToFile(url, file);
        return file;
    }

    public void deleteFile(File file) throws IOException {
        FileUtils.forceDelete(file);
    }
}
