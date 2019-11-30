package itx.fs.service;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import itx.fs.service.client.LoggingObservableEmitter;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;

import java.nio.file.Path;

/**
 * File system Scanner service.
 */
public interface FSService {

    /**
     * Get Observable stream for given query. Consumer of this stream data should subscribe with own subscriber.
     * @param query file system query.
     * @return file system data stream. See {@link LoggingObservableEmitter} as simple example.
     */
    Observable<DirItem> scanDirectoryAsync(DirQuery query);

    /**
     * Get {@link DirItem} data for single file or directory.
     * @param filePath file or directory path to be scanned.
     * @return file or directory data or error.
     */
    Single<DirItem> scanSingleFileOrDirectory(Path filePath);

}
