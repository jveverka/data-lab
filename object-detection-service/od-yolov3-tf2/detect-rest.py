#!/usr/bin/env python3

import time
import sys
from flask import Flask, request, jsonify, abort
from absl import app, flags, logging
from absl.flags import FLAGS
import numpy as np
import tensorflow as tf
from yolov3_tf2.models import YoloV3
from yolov3_tf2.dataset import transform_images

flags.DEFINE_string('classes', './data/coco.names', 'path to classes file')
flags.DEFINE_string('weights', './checkpoints/yolov3.tf', 'path to weights file')
flags.DEFINE_integer('size', 416, 'resize images to')
flags.DEFINE_integer('num_classes', 80, 'number of classes in the model')
flags.DEFINE_integer('port', 5000, 'http port')

app = Flask(__name__)
global yolo

@app.route('/version')
def getVersion():
    version = { "version": "1.0.0" }
    return jsonify(version)

@app.route('/local-detect', methods=["POST"])
def detect():
    req_data = request.get_json()
    path = req_data['path'];
    logging.info('path:{}'.format(path))

    content = open(path, 'rb').read()
    return evaluateImage(content, path)

@app.route('/upload-detect', methods=["POST"])
def uploadDetect():
    if 'file' not in request.files:
        logging.info('Error: file not attached !')
        abort(500)
    file = request.files['file']
    if file.filename == '':
        logging.info('Error: file name not specified !')
        abort(500)
    content = file.stream.read()
    return evaluateImage(content, file.filename)


def evaluateImage(content, path):
    img = tf.image.decode_image(content, channels=3)
    img = tf.expand_dims(img, 0)
    img = transform_images(img, FLAGS.size)

    t1 = time.time()
    boxes, scores, classes, nums = yolo(img)
    t2 = time.time() - t1
    logging.info('time: {}'.format(t2))

    logging.info('detections:')
    objects = list(range(nums[0]))
    for i in range(nums[0]):
        object = {
            "class": class_names[int(classes[0][i])],
            "score": np.array(scores[0][i]).tolist(),
            "box": np.array(boxes[0][i]).tolist()
        }
        objects[i] = object
    result = { 'result': 'ok', 'path': path, 'time': t2, "objects": objects }
    return result


if __name__ == '__main__':
    try:
       FLAGS(sys.argv)
       global yolo
       physical_devices = tf.config.experimental.list_physical_devices('GPU')
       if len(physical_devices) > 0:
          tf.config.experimental.set_memory_growth(physical_devices[0], True)
       yolo = YoloV3(classes=FLAGS.num_classes)
       yolo.load_weights(FLAGS.weights)
       logging.info('weights loaded')

       class_names = [c.strip() for c in open(FLAGS.classes).readlines()]
       logging.info('classes loaded')

       app.run(debug=False, port=FLAGS.port)
    except SystemExit:
       pass
