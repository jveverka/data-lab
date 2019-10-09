package itx.dataserver.services.filescanner;

import itx.dataserver.services.filescanner.dto.FileInfo;
import itx.dataserver.services.filescanner.dto.FileInfoId;
import itx.dataserver.services.filescanner.dto.FileMetaData;
import itx.dataserver.services.filescanner.dto.FileSystemInfo;
import itx.dataserver.services.filescanner.dto.FileType;
import itx.dataserver.services.filescanner.dto.metadata.MetaDataContainer;
import itx.fs.service.dto.DirItem;
import itx.image.service.model.MetaData;

import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class DataUtils {

    private DataUtils() {
    }

    public static FileInfoId createFileInfoId(DirItem dirItem) throws NoSuchAlgorithmException {
        String checkSumStr = dirItem.getCheckSum().isPresent() ? dirItem.getCheckSum().get().toString() : "";
        String id = dirItem.getPath().toString() + ":" + checkSumStr + ":" + dirItem.getAttributes().lastModifiedTime().toString();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(id.getBytes());
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return new FileInfoId(result.toString());
    }

    public static FileType createFileType(DirItem dirItem) {
        if (dirItem.getAttributes().isRegularFile()) {
            return FileType.REGULAR_FILE;
        } else if (dirItem.getAttributes().isDirectory()) {
            return FileType.DIRECTORY;
        } else if (dirItem.getAttributes().isSymbolicLink()) {
            return FileType.SYMBOLIC_LINK;
        } else {
            return FileType.OTHER;
        }
    }

    public static FileSystemInfo createFileSystemInfo(DirItem dirItem) {
        FileType type = createFileType(dirItem);
        return new FileSystemInfo(dirItem.getPath().toString(), dirItem.getCheckSum(),
                dirItem.getAttributes().creationTime(), dirItem.getAttributes().lastModifiedTime(),
                dirItem.getAttributes().lastAccessTime(), type, dirItem.getAttributes().size());
    }

    public static FileMetaData createMetaDataContainer(Optional<MetaData> metaData) {
        List<MetaDataContainer<?>> metaDataContainers = new ArrayList<>();
        return new FileMetaData(metaDataContainers);
    }

    public static FileMetaData createMetaDataFromSource(Map<String, Object> source) {
        List<MetaDataContainer<?>> metaDataContainers = new ArrayList<>();
        return new FileMetaData(metaDataContainers);
    }

    public static FileInfo createFileInfo(DirItem dirItem, Optional<MetaData> metaData) throws NoSuchAlgorithmException {
        FileInfoId id = createFileInfoId(dirItem);
        FileSystemInfo fileSystemInfo = createFileSystemInfo(dirItem);
        FileMetaData mediaInfo = createMetaDataContainer(metaData);
        return new FileInfo(id, fileSystemInfo, mediaInfo);
    }

    public static FileTime createFileTime(String timeStamp) {
        long timeStampLong = Long.parseLong(timeStamp);
        return FileTime.fromMillis(timeStampLong);
    }

}
