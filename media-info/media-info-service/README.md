# Media Info Service
This service extract meta-data from media files like jpge/png images, video files and similar.

### Build and Run
```
gradle clean build test publishToMavenLocal
```

### Use in your project
* Use ``MediaService`` to get MetaData about media files.
  ```
  MediaService mediaService = new MediaServiceImpl();
  Optional<MetaData> metaData = mediaService.getMetaData(inputStream);
  ```
* Use ``ParsingUtils`` to serialize and deserialize MetaData to / from json.
  ```
  String jsonData = ParsingUtils.writeAsJsonString(metaData);
  metaData = ParsingUtils.readFromJsonString(jsonData);
  ```