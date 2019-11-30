package itx.fs.service.fsaccess;

import itx.fs.service.dto.CheckSum;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

/**
 * This is low level interface for interaction with file system.
 */
public interface FileDataReader {

    /**
     * Get basic file attributes for given path.
     * @param path target path.
     * @return {@link BasicFileAttributes} for given path.
     * @throws IOException
     */
    BasicFileAttributes getBasicFileAttributes(Path path) throws IOException;

    /**
     * Get list of names of files and directories within target path.
     * The list is level 1 depth content of target directory.
     * @param path target path.
     * @return List of files and directories within target path or empty if target path is not directory.
     */
    Optional<String[]> getDirectoryList(Path path);

    /**
     * Return true if target path is directory, false otherwise.
     * @param path target path.
     * @return
     */
    boolean isDirectory(Path path);

    /**
     * Calculate SHA-256 checksum for file of give path.
     * @param path target path.
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    CheckSum calculateSha256Checksum(Path path) throws IOException, NoSuchAlgorithmException;

}
