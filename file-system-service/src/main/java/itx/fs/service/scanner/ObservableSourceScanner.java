package itx.fs.service.scanner;

import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Cancellable;
import itx.fs.service.fsaccess.FSUtils;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;
import itx.fs.service.fsaccess.FileDataReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class ObservableSourceScanner implements Disposable, Cancellable, ObservableOnSubscribe<DirItem> {

    private static final Logger LOG = LoggerFactory.getLogger(ObservableSourceScanner.class);

    private final Executor executor;
    private final DirQuery query;
    private final FileDataReader fileDataReader;

    private boolean dispose;

    public ObservableSourceScanner(DirQuery query, Executor executor, FileDataReader fileDataReader) {
        this.query = query;
        this.executor = executor;
        this.fileDataReader = fileDataReader;
    }

    @Override
    public void subscribe(ObservableEmitter<DirItem> emitter) {
        LOG.info("subscribe: {}",  query.getPath());
        dispose = false;
        emitter.setCancellable(this);
        emitter.setDisposable(this);
        executor.execute(() -> {
                try {
                    TransformingEmitter transformingEmitter = new TransformingEmitter(emitter, query.getExecutorSize());
                    FSUtils.walkDirectoryRecursively(query.getPath(), transformingEmitter, fileDataReader);
                    while (!transformingEmitter.await(1, TimeUnit.SECONDS));
                    transformingEmitter.close();
                } catch (Exception e) {
                    LOG.error("Error: ", e);
                }
        });
    }

    @Override
    public void dispose() {
        dispose = true;
    }

    @Override
    public boolean isDisposed() {
        return dispose;
    }

    @Override
    public void cancel() throws Throwable {
        LOG.info("cancel");
    }

}
