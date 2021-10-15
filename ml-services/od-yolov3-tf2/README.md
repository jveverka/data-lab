# YOLOv3 Object Detection Service
This object detection service is based on [YOLOv3](https://pjreddie.com/darknet/yolo/) 
and [TensorFlow 2.0](https://www.tensorflow.org/guide/effective_tf2).
This service is designed to detect objects in images or videos. 
Source images are loaded from local disk, or uploaded in http post multipart request.
Supported image formats: JPEG, PNG.

## Install Environment
* Ubuntu __20.04 LTS__
* Python __3.8.x__
* Install [Tensorflow, nVidia CUDA + cuDNN](https://github.com/jveverka/guildelines-and-procedures/tree/master/ubuntu-notes/cuda-and-tensorflow)
* Python modules
  ```
  pip3 install --user Flask==1.1.2
  ``` 
* download pre-trained model
  ```
  wget https://pjreddie.com/media/files/yolov3.weights -O data/yolov3.weights
  sha256sum data/yolov3.weights
  # 523e4e69e1d015393a1b0a441cef1d9c7659e3eb2d7e15f793f060a21b32f297
  ```
* convert weights using CLI
  ```
  ./convert.py
  ```  

## Run Image Object detection - CLI  
```
./detect-image.py --image path/to/image/file.jpg
```
Check console output and ``output.jpg`` image for generated results.

## Run Video Object detection - CLI
```
# from file
./detect-video.py --video path/to/video/file.mp4

# webcam
./detect-video.py --video 0
```
Annotated video frames are displayed on UI in new window.

## Run Image Object detection - REST service
* Start web server. 
  ```
  ./detect-image-rest.py 
  ```
* Get service version, response is JSON formatted version.  
  ```
  curl -X GET http://localhost:5000/version
  ```
* Detect objects in image on local file system, response is JSON formatted object detections.  
  ```
  curl -X POST -H "Content-Type: application/json" -d '{ "path": "/local/path/to/image.jpg" }' http://localhost:5000/local-detect
  ```
* Detect objects in uploaded image, response is JSON formatted object detections.   
  ```
  curl -X POST -H "Content-Type: multipart/form-data" -F "file=@/path/to/image.jpg" http://localhost:5000/upload-detect
  ```

## Java REST client API
* [Java REST API client](../od-yolov3-tf2-java) for YOLOv3 Object Detection Service is available here.

#### References
Implementation uses or is inspired by those projects:
* [YoloV3 Implemented in TensorFlow 2.0](https://github.com/zzh8829/yolov3-tf2)
* [Keras YoloV3 for TF 1.12](https://github.com/qqwweee/keras-yolo3)
* [Python Flask](https://flask.palletsprojects.com/en/1.1.x/)
  