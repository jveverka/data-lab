package itx.dataserver.services.mlscanner;

import itx.elastic.service.ElasticSearchService;
import itx.fs.service.dto.DirItem;
import itx.ml.service.odyolov3tf2.http.client.ObjectRecognitionService;

import java.util.concurrent.Executor;

public class MlScannerServiceImpl implements MlScannerService {

    private final Executor executor;
    private final ElasticSearchService elasticSearchService;
    private final ObjectRecognitionService objectRecognitionService;

    public MlScannerServiceImpl(Executor executor, ElasticSearchService elasticSearchService,
                                ObjectRecognitionService objectRecognitionService) {
        this.executor = executor;
        this.elasticSearchService = elasticSearchService;
        this.objectRecognitionService = objectRecognitionService;
    }

    @Override
    public void onFileEvent(DirItem dirItem) {
        MlScanTask mlScanTask = new MlScanTask(elasticSearchService, objectRecognitionService, dirItem);
        executor.execute(mlScanTask);
    }

}
