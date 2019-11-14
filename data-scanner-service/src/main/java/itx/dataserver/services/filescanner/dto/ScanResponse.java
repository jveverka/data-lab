package itx.dataserver.services.filescanner.dto;

import java.nio.file.Path;

public class ScanResponse {

    private final Path path;
    private final long deletedRecords;
    private final long createdRecords;
    private final boolean success;
    private final long directories;
    private final long errors;

    public ScanResponse(Path path, long deletedRecords, long createdRecords, boolean success, long directories, long errors) {
        this.path = path;
        this.deletedRecords = deletedRecords;
        this.createdRecords = createdRecords;
        this.success = success;
        this.directories = directories;
        this.errors = errors;
    }

    public Path getPath() {
        return path;
    }

    public long getDeletedRecords() {
        return deletedRecords;
    }

    public long getCreatedRecords() {
        return createdRecords;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getDirectories() {
        return directories;
    }

    public long getErrors() {
        return errors;
    }

    public static ScanResponse getError(Path path) {
        return new ScanResponse(path, 0, 0, false, 0, 0);
    }

    public static ScanResponse getSuccess(Path path, long createdRecords, long deletedRecords, long directories, long errors) {
        return new ScanResponse(path, deletedRecords, createdRecords, true, directories, errors);
    }

}
