package itx.dataserver.services.filescanner.dto;

import itx.dataserver.services.filescanner.dto.content.ContentAnnotationHolder;

import java.util.Collection;

public class ContentAnnotationContainer {

    private final Collection<ContentAnnotationHolder<?>> annotations;

    public ContentAnnotationContainer(Collection<ContentAnnotationHolder<?>> annotations) {
        this.annotations = annotations;
    }

    public Collection<ContentAnnotationHolder<?>> getAnnotations() {
        return annotations;
    }

}
