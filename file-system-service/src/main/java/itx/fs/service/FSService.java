package itx.fs.service;

import io.reactivex.rxjava3.core.Flowable;
import itx.fs.service.dto.DirItem;

import java.nio.file.Path;

public interface FSService {

    Flowable<DirItem> scanDirectory(Path path);

}
