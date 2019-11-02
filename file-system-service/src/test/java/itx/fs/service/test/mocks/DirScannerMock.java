package itx.fs.service.test.mocks;

import io.reactivex.rxjava3.core.FlowableEmitter;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;
import itx.fs.service.scanner.DirScanner;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;

public class DirScannerMock implements DirScanner {

    @Override
    public void scanDirectory(FlowableEmitter<DirItem> emitter, DirQuery query) throws IOException {
        BasicFileAttributes fileAttributesMock = Mockito.mock(BasicFileAttributes.class);
        emitter.onNext(new DirItem(query.getPath(), fileAttributesMock));
    }

}
