package itx.dataserver.services.mlscanner;

import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.dataserver.services.mlscanner.dto.ObjectRecognition;
import itx.elastic.service.ElasticSearchService;
import itx.fs.service.dto.DirItem;
import itx.ml.service.odyolov3tf2.http.client.ObjectRecognitionService;
import itx.ml.service.odyolov3tf2.http.client.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import itx.dataserver.services.filescanner.DataUtils;

public class MlScanTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(MlScanTask.class);

    private final ElasticSearchService elasticSearchService;
    private final ObjectRecognitionService objectRecognitionService;
    private final DirItem dirItem;

    public MlScanTask(ElasticSearchService elasticSearchService, ObjectRecognitionService objectRecognitionService,
                      DirItem dirItem) {
        this.elasticSearchService = elasticSearchService;
        this.objectRecognitionService = objectRecognitionService;
        this.dirItem = dirItem;
    }

    @Override
    public void run() {
        try {
            Result result = objectRecognitionService.getResult(dirItem.getPath());
            if (result.getResult() && result.getObjects().size() > 0) {
                FileInfoId fileInfoId = DataUtils.createFileInfoId(dirItem);
                ObjectRecognition objectRecognition = new ObjectRecognition(fileInfoId, result.getPath(), result.getObjects());
                elasticSearchService.saveDocument(ObjectRecognition.class, objectRecognition);
            } else {
                LOG.warn("No objects recognized in image.");
            }
        } catch (IOException e) {
            LOG.error("Error: ", e);
        } catch (InterruptedException e) {
            LOG.error("Error: ", e);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Error: ", e);
        }
    }

}
