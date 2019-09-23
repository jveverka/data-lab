package itx.fs.service;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import itx.fs.service.dto.DirItem;

import java.nio.file.Path;
import java.util.concurrent.Executor;

public class FSServiceImpl implements FSService {

    private final Executor executor;

    public FSServiceImpl(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Flowable<DirItem> scanDirectory(Path rootPath) {
        return Flowable.create(new DirItemSource(executor, rootPath), BackpressureStrategy.BUFFER);
    }

}
