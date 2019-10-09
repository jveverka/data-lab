package itx.dataserver.services.filescanner.dto.content;

public interface ContentAnnotationHolder<T> {

    T getContentAnnotation();

    ContentAnnotationType getType();

}
