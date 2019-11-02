package itx.dataserver.services.filescanner.dto;

import java.nio.file.Path;

public class ScanResponse {

    private final Path path;
    private final long deletedRecords;
    private final long createdRecords;
    private final boolean success;

    public ScanResponse(Path path, long deletedRecords, long createdRecords, boolean success) {
        this.path = path;
        this.deletedRecords = deletedRecords;
        this.createdRecords = createdRecords;
        this.success = success;
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

    public static ScanResponse getError(Path path) {
        return new ScanResponse(path, 0, 0, false);
    }

    public static ScanResponse getSuccess(Path path, long createdRecords, long deletedRecords) {
        return new ScanResponse(path, deletedRecords, createdRecords, true);
    }

}
