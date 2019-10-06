package itx.fs.service.scanner;

import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Cancellable;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ObservableSourceScanner implements Disposable, Cancellable, ObservableOnSubscribe<DirItem> {

    private static final Logger LOG = LoggerFactory.getLogger(ObservableSourceScanner.class);

    private final Executor executor;
    private final DirQuery query;

    private boolean dispose;

    public ObservableSourceScanner(DirQuery query, Executor executor) {
        this.query = query;
        this.executor = executor;
    }

    @Override
    public void subscribe(ObservableEmitter<DirItem> emitter) throws Throwable {
        dispose = false;
        emitter.setCancellable(this);
        emitter.setDisposable(this);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ExecutorService executorService = Executors.newFixedThreadPool(query.getExecutorSize());
                    Files.walkFileTree(query.getPath(), new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) {
                            executorService.submit(new FileScannerTask(emitter, path, attributes));
                            return FileVisitResult.CONTINUE;
                        }
                    });
                    executorService.shutdown();
                    executorService.awaitTermination(1, TimeUnit.HOURS);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
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

    }

}
