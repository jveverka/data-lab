package itx.dataserver.services.filescanner.dto.fileinfo;

import itx.dataserver.services.filescanner.DataUtils;
import itx.elastic.service.DataTransformer;
import itx.elastic.service.dto.DocumentId;
import itx.fs.service.dto.CheckSum;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class FileInfoDataTransformer implements DataTransformer<FileInfo> {

    @Override
    public Map<String, Object> getSource(FileInfo data) {
        Map<String, Object> checksum = new HashMap<>();
        if (data.getFileSystemInfo().getChecksum().isPresent()) {
            checksum.put("checksum", data.getFileSystemInfo().getChecksum().get().getChecksum());
            checksum.put("algorithm", data.getFileSystemInfo().getChecksum().get().getAlgorithm());
        }
        Map<String, Object> source = new HashMap<>();
        source.put("fileInfoId", data.getId().getId());
        source.put("path", data.getFileSystemInfo().getPath());
        source.put("checksum", checksum);
        source.put("creationTime", data.getFileSystemInfo().getCreationTime().toMillis());
        source.put("lastModifiedTime", data.getFileSystemInfo().getLastModifiedTime().toMillis());
        source.put("lastAccessTime", data.getFileSystemInfo().getLastAccessTime().toMillis());
        source.put("type", data.getFileSystemInfo().getType().name());
        if (data.getFileSystemInfo().getExtension().isPresent()) {
            source.put("extension", data.getFileSystemInfo().getExtension().get());
        }
        source.put("size", data.getFileSystemInfo().getSize());
        return source;
    }

    @Override
    public XContentBuilder getIndexMapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                DataUtils.addMappingField(builder, "fileInfoId", "keyword");
                DataUtils.addMappingField(builder, "path", "text");
                DataUtils.addMappingField(builder, "creationTime", "date");
                DataUtils.addMappingField(builder, "lastModifiedTime", "date");
                DataUtils.addMappingField(builder, "lastAccessTime", "date");
                DataUtils.addMappingField(builder, "type", "keyword");
                DataUtils.addMappingField(builder, "size", "long");
                DataUtils.addMappingField(builder, "extension", "keyword");
                DataUtils.addMappingField(builder, "checksum.checksum", "keyword");
                DataUtils.addMappingField(builder, "checksum.algorithm", "keyword");
            }
            builder.endObject();
        }
        builder.endObject();
        return builder;
    }

    @Override
    public String getIndexName() {
        return "file-info";
    }

    @Override
    public DocumentId getDocumentId(FileInfo data) {
        return new DocumentId(data.getId().getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public FileInfo getInstance(DocumentId id, Map<String, Object> source) {
        FileInfoId fileInfoId = new FileInfoId(id.getId());

        Optional<CheckSum> checkSumOptional = Optional.empty();
        if (source.get("checksum") != null) {
            Map<String, Object> checksum = (Map<String, Object>)source.get("checksum");
            CheckSum checkSum = new CheckSum(checksum.get("checksum").toString(), checksum.get("algorithm").toString());
            checkSumOptional = Optional.of(checkSum);
        }
        Optional<String> extension = Optional.empty();
        if (source.get("extension") != null) {
            extension = Optional.of(source.get("extension").toString());
        }
        FileSystemInfo fileSystemInfo = new FileSystemInfo(source.get("path").toString(), checkSumOptional,
                DataUtils.createFileTime(source.get("creationTime").toString()),
                DataUtils.createFileTime(source.get("lastModifiedTime").toString()),
                DataUtils.createFileTime(source.get("lastAccessTime").toString()),
                FileType.valueOf(source.get("type").toString()),
                Long.parseLong(source.get("size").toString()), extension
        );

        return new FileInfo(fileInfoId, fileSystemInfo);
    }

}
