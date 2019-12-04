# YOLOv3 Object Detection Service
This object detection service is based on [YOLOv3](https://pjreddie.com/darknet/yolo/) 
and [TensorFlow 2.0](https://www.tensorflow.org/guide/effective_tf2).
This service is designed to detect objects in images or videos. 

## Install Environment
* Ubuntu __18.04.3 LTS__
* Python __3.6.9__
* Python modules
  ```
  pip3 install --user tensorflow==2.0.0b1
  pip3 install --user opencv-python==4.1.1.26
  pip3 install --user flask
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

## Run Object detection - CLI  
```
./detect.py --image path/to/image/file.jpg
```
Check console output and ``output.jpg`` image for generated results.

## Run Object detection - REST service
```
./detect-rest.py 
curl -X POST http://localhost:5000/local-detect -d '{ "path": "/local/path/to/image.jpg" }' -H "Content-Type: application/json"
```

#### Sources
Implementation is inspired by those projects:
* [YoloV3 Implemented in TensorFlow 2.0](https://github.com/zzh8829/yolov3-tf2)
* [Keras YoloV3 for TF 1.12](https://github.com/qqwweee/keras-yolo3)
  