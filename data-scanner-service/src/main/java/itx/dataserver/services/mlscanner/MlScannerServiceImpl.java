package itx.dataserver.services.mlscanner;

import itx.elastic.service.ElasticSearchService;
import itx.fs.service.dto.DirItem;
import itx.ml.service.odyolov3tf2.http.client.ObjectRecognitionService;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

public class MlScannerServiceImpl implements MlScannerService {

    private final Executor executor;
    private final ElasticSearchService elasticSearchService;
    private final ObjectRecognitionService objectRecognitionService;
    private final AtomicLong atomicLong;

    public MlScannerServiceImpl(Executor executor, ElasticSearchService elasticSearchService,
                                ObjectRecognitionService objectRecognitionService) {
        this.executor = executor;
        this.elasticSearchService = elasticSearchService;
        this.objectRecognitionService = objectRecognitionService;
        this.atomicLong = new AtomicLong(0);
    }

    @Override
    public void onFileEvent(DirItem dirItem) {
        MlScanTask mlScanTask = new MlScanTask(elasticSearchService, objectRecognitionService, dirItem);
        atomicLong.incrementAndGet();
        executor.execute(mlScanTask);
    }

    @Override
    public long getTaskCount() {
        return atomicLong.get();
    }

}
