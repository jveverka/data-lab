package itx.dataserver.services.filescanner.dto;

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
        Map<String, Object> fileSystemInfo = new HashMap<>();
        fileSystemInfo.put("path", data.getFileSystemInfo().getPath());
        fileSystemInfo.put("checksum", checksum);
        fileSystemInfo.put("creationTime", data.getFileSystemInfo().getCreationTime().toMillis());
        fileSystemInfo.put("lastModifiedTime", data.getFileSystemInfo().getLastModifiedTime().toMillis());
        fileSystemInfo.put("lastAccessTime", data.getFileSystemInfo().getLastAccessTime().toMillis());
        fileSystemInfo.put("type", data.getFileSystemInfo().getType().name());
        fileSystemInfo.put("size", data.getFileSystemInfo().getSize());
        Map<String, Object> metaData = new HashMap<>();
        Map<String, Object> source = new HashMap<>();
        source.put("fileSystemInfo", fileSystemInfo);
        source.put("metaData", metaData);
        return source;
    }

    @Override
    public XContentBuilder getIndexMapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("fileSystemInfo");
                {
                    builder.field("path", "text");
                    builder.field("creationTime", "date");
                    builder.field("lastModifiedTime", "date");
                    builder.field("lastAccessTime", "date");
                    builder.field("type", "keyword");
                    builder.field("size", "long");
                    builder.startObject("checksum");
                    {
                        builder.field("checksum", "keyword");
                        builder.field("algorithm", "keyword");
                    }
                    builder.endObject();
                }
                builder.endObject();
                builder.startObject("metaData");
                {
                    builder.field("type", "text");
                }
                builder.endObject();
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
    public FileInfo getInstance(DocumentId id, Map<String, Object> source) {
        FileInfoId fileInfoId = new FileInfoId(id.getId());

        Map<String, Object> fsInfo = (Map<String, Object>)source.get("fileSystemInfo");
        Optional<CheckSum> checkSumOptional = Optional.empty();
        if (fsInfo.get("checksum") != null) {
            Map<String, Object> checksum = (Map<String, Object>)source.get("checksum");
            CheckSum checkSum = new CheckSum(checksum.get("checksum").toString(), checksum.get("algorithm").toString());
            checkSumOptional = Optional.of(checkSum);
        }
        FileSystemInfo fileSystemInfo = new FileSystemInfo(fsInfo.get("path").toString(), checkSumOptional,
                DataUtils.createFileTime(fsInfo.get("creationTime").toString()),
                DataUtils.createFileTime(fsInfo.get("lastModifiedTime").toString()),
                DataUtils.createFileTime(fsInfo.get("lastAccessTime").toString()),
                FileType.valueOf(fsInfo.get("type").toString()),
                Long.parseLong(fsInfo.get("size").toString())
                );

        MetaData metaData = new MetaData();
        return new FileInfo(fileInfoId, fileSystemInfo, metaData);
    }

}
