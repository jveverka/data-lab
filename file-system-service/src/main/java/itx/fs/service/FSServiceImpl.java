package itx.fs.service;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;
import itx.fs.service.scanner.DirItemSource;
import itx.fs.service.scanner.DirScanner;
import itx.fs.service.scanner.FileSystemDirScanner;

import java.util.concurrent.Executor;

public class FSServiceImpl implements FSService {

    private final Executor executor;
    private final DirScanner dirScanner;

    public FSServiceImpl(Executor executor) {
        this.executor = executor;
        this.dirScanner = new FileSystemDirScanner();
    }

    public FSServiceImpl(Executor executor, DirScanner dirScanner) {
        this.executor = executor;
        this.dirScanner = dirScanner;
    }

    @Override
    public Flowable<DirItem> scanDirectory(DirQuery query) {
        return Flowable.create(new DirItemSource(executor, query, dirScanner), BackpressureStrategy.BUFFER);
    }

}
