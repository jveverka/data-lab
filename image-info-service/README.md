# Image Info Service

### Build and Run
```
gradle clean build test publishToMavenLocal
```

### Use in your project
* Use ``Imageservice`` to get MetaData about media files.
  ```
  ImageService imageService = new ImageServiceImpl();
  Optional<MetaData> metaData = imageService.getMetaData(inputStream);
  ```
* Use ``ParsingUtils`` to serialize and deserialize MetaData to / from json.
  ```
  String jsonData = ParsingUtils.writeAsJsonString(metaData);
  metaData = ParsingUtils.readFromJsonString(jsonData);
  ```