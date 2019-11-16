package itx.dataserver.services.filescanner.dto.metadata.annotation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import itx.dataserver.services.filescanner.dto.fileinfo.FileInfoId;
import itx.dataserver.services.filescanner.dto.metadata.Coordinates;

import java.nio.file.Path;
import java.util.Date;

public class AnnotationMetaData {

    private final FileInfoId id;
    private final Path path;
    private final String name;
    private final String description;
    private final Date timeStamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Coordinates coordinates;

    @JsonCreator
    public AnnotationMetaData(@JsonProperty("path") Path path,
                              @JsonProperty("name") String name,
                              @JsonProperty("description") String description,
                              @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
                              @JsonProperty("timeStamp") Date timeStamp,
                              @JsonProperty("coordinates") Coordinates coordinates) {
        this.id = null;
        this.path = path;
        this.name = name;
        this.description = description;
        this.timeStamp = timeStamp;
        this.coordinates = coordinates;
    }

    public AnnotationMetaData(FileInfoId id, Path path, String name, String description, Date timeStamp, Coordinates coordinates) {
        this.id = id;
        this.path = path;
        this.name = name;
        this.description = description;
        this.timeStamp = timeStamp;
        this.coordinates = coordinates;
    }

    @JsonIgnore
    public FileInfoId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Path getPath() {
        return path;
    }

    public static AnnotationMetaData from(FileInfoId id, AnnotationMetaData annotationMetaData) {
        return new AnnotationMetaData(id, annotationMetaData.path, annotationMetaData.name,
                annotationMetaData.description, annotationMetaData.timeStamp, annotationMetaData.coordinates);
    }

    public static AnnotationMetaData from(Path path, AnnotationMetaData annotationMetaData) {
        return new AnnotationMetaData(annotationMetaData.id, path, annotationMetaData.name,
                annotationMetaData.description, annotationMetaData.timeStamp, annotationMetaData.coordinates);
    }

}
