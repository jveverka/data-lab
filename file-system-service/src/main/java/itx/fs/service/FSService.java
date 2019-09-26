package itx.fs.service;

import io.reactivex.rxjava3.core.Flowable;
import itx.fs.service.dto.DirItem;
import itx.fs.service.dto.DirQuery;


public interface FSService {

    Flowable<DirItem> scanDirectory(DirQuery query);

}
