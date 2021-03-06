package itx.fs.service.fsaccess;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.reactivex.rxjava3.core.Emitter;
import itx.fs.service.dto.CheckSum;
import itx.fs.service.dto.FileItem;

public class FSUtils {

    public static final String SHA256 = "SHA-256";

    private FSUtils() {
    }

    /**
     * Compute SHA-256 checksum of file.
     * @param file {@link Path} to file.
     * @return Calculated SHA-256 {@link CheckSum} for given file.
     * @throws NoSuchAlgorithmException thrown in case SHA-256 algorithm is not available.
     * @throws IOException thrown in case file reading error.
     */
    public static CheckSum calculateSha256Checksum(Path file) throws NoSuchAlgorithmException, IOException {
        return calculateChecksum(file, SHA256);
    }

    /**
     * Compute checksum of file with given algorithm.
     * @param file {@link Path} to file.
     * @param algorithm Algorithm name. For example SHA-256.
     * @return Calculated {@link CheckSum} for given file.
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static CheckSum calculateChecksum(Path file, String algorithm) throws NoSuchAlgorithmException, IOException {
        try (InputStream fis = new FileInputStream(file.toFile())) {
            return calculateChecksum(fis, algorithm);
        }
    }

    /**
     * Compute checksum from {@link InputStream} data with given algorithm.
     * @param {@link InputStream} of data for checksum calculation.
     * @param algorithm Algorithm name. For example SHA-256.
     * @return Calculated {@link CheckSum} for given file.
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static CheckSum calculateChecksum(InputStream is, String algorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] buffer = new byte[10240];
        int nread;
        while ((nread = is.read(buffer)) != -1) {
            digest.update(buffer, 0, nread);
        }
        StringBuilder result = new StringBuilder();
        for (byte b : digest.digest()) {
            result.append(String.format("%02x", b));
        }
        return new CheckSum(result.toString(), algorithm);
    }

    /**
     * Get file extension from {@link Path}
     * @param path {@link Path} to file.
     * @param isRegularFile flag if the Path point to file or directory.
     * @return file extension or empty if extension is not present or path point to directory.
     */
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

    /**
     * Walk through single directory, emit observed files and sub-directories. This walk is NOT recursive.
     * @param dirPath base {@link Path} to walk through.
     * @param emitter {@link Emitter} which consumes walk through events.
     * @param fileDataReader service reading data from file system.
     * @return {@link List} of sub-directories which may be used by recursive walk trough.
     * @throws IOException thrown in case of file access error.
     */
    public static List<Path> walkDirectory(Path dirPath, Emitter<FileItem> emitter, FileDataReader fileDataReader) throws IOException {
        List<Path> directories = new ArrayList<>();
        Optional<String[]> directoryList = fileDataReader.getDirectoryList(dirPath);
        if (directoryList.isPresent()) {
            String[] fileListing = directoryList.get();
            for (String subPathString: fileListing) {
                Path subPath = Paths.get(dirPath.toString(), subPathString);
                if (fileDataReader.isDirectory(subPath)) {
                    directories.add(subPath);
                    BasicFileAttributes basicFileAttributes = fileDataReader.getBasicFileAttributes(subPath);
                    emitter.onNext(new FileItem(subPath, basicFileAttributes));
                } else {
                    try {
                        BasicFileAttributes basicFileAttributes = fileDataReader.getBasicFileAttributes(subPath);
                        emitter.onNext(new FileItem(subPath, basicFileAttributes));
                    } catch (IOException e) {
                        emitter.onError(e);
                        throw e;
                    }
                }
            }
        } else {
            IOException e = new IOException("Directory expected !");
            emitter.onError(e);
            throw e;
        }
        return directories;
    }

    /**
     * Walk through directory, emit observed files and sub-directories. This walk is recursive.
     * @param dirPath base {@link Path} to walk through.
     * @param emitter which consumes walk through events.
     * @param fileDataReader service reading data from file system.
     * @throws IOException thrown in case of file access error.
     */
    public static void walkDirectoryRecursively(Path dirPath, Emitter<FileItem> emitter, FileDataReader fileDataReader) throws IOException {
        List<Path> dirs = walkDirectory(dirPath, emitter, fileDataReader);
        while (!dirs.isEmpty()) {
            List<Path> subDirs = new ArrayList<>();
            for (Path subDirPath : dirs) {
                List<Path> subCollection = walkDirectory(subDirPath, emitter, fileDataReader);
                subDirs.addAll(subCollection);
            }
            dirs = subDirs;
        }
        emitter.onComplete();
    }

}
