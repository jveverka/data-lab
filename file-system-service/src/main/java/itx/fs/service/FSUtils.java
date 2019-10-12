package itx.fs.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import itx.fs.service.dto.CheckSum;

public class FSUtils {

    public static final String SHA256 = "SHA-256";

    private FSUtils() {
    }

    public static CheckSum calculateChecksum(Path file, String algorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        try (InputStream fis = new FileInputStream(file.toFile())) {
            byte[] buffer = new byte[10240];
            int nread;
            while ((nread = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, nread);
            }
        }
        StringBuilder result = new StringBuilder();
        for (byte b : digest.digest()) {
            result.append(String.format("%02x", b));
        }
        return new CheckSum(result.toString(), algorithm);
    }


    public static Optional<String> getFileExtension(Path path, boolean isRegularFile) {
        if (!isRegularFile) {
            return Optional.empty();
        }
        String pathStr = path.toString();
        int i = pathStr.lastIndexOf('.');
        if (i==-1) {
            return Optional.empty();
        } else {
            String extension = pathStr.substring(i + 1, pathStr.length());
            return Optional.ofNullable("".equals(extension) ? null : extension);
        }
    }

}
