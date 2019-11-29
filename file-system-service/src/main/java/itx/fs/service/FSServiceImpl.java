package itx.fs.service;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;
import itx.fs.service.fsaccess.FileDataReader;
import itx.fs.service.fsaccess.FileDataReaderImpl;
import itx.fs.service.scanner.ObservableSourceScanner;
import itx.fs.service.scanner.SingleOnSubscribeScanner;

import java.nio.file.Path;
import java.util.concurrent.Executor;

public class FSServiceImpl implements FSService {

    private final Executor executor;
    private final FileDataReader fileDataReader;

    public FSServiceImpl(Executor executor) {
        this.executor = executor;
        this.fileDataReader = new FileDataReaderImpl();
    }

    public FSServiceImpl(Executor executor, FileDataReader fileDataReader) {
        this.executor = executor;
        this.fileDataReader = fileDataReader;
    }

    @Override
    public Observable<DirItem> scanDirectoryAsync(DirQuery query) {
        return Observable.create(new ObservableSourceScanner(query, executor, fileDataReader));
    }

    @Override
    public Single<DirItem> scanSingleFileOrDirectory(Path filePath) {
        return Single.create(new SingleOnSubscribeScanner(executor, filePath));
    }

}
