package itx.fs.service;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;
import itx.fs.service.scanner.ObservableSourceScanner;
import itx.fs.service.scanner.SingleOnSubscribeScanner;

import java.nio.file.Path;
import java.util.concurrent.Executor;

public class FSServiceImpl implements FSService {

    private final Executor executor;

    public FSServiceImpl(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Observable<DirItem> scanDirectoryAsync(DirQuery query) {
        return Observable.create(new ObservableSourceScanner(query, executor));
    }

    @Override
    public Single<DirItem> scanSingleFileOrDirectory(Path filePath) {
        return Single.create(new SingleOnSubscribeScanner(executor, filePath));
    }

}
