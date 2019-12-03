#!/usr/bin/env python3

from flask import Flask, request, jsonify
from absl import app, flags, logging
from absl.flags import FLAGS
import tensorflow as tf
from yolov3_tf2.models import YoloV3

flags.DEFINE_string('classes', './data/coco.names', 'path to classes file')
flags.DEFINE_string('weights', './checkpoints/yolov3.tf', 'path to weights file')
flags.DEFINE_integer('size', 416, 'resize images to')
flags.DEFINE_integer('num_classes', 80, 'number of classes in the model')

app = Flask(__name__)

@app.route('/version')
def getVersion():
    return '{ "version": "1.0.0" }'

@app.route('/detect', methods=["POST"])
def detect():
    req_data = request.get_json()
    return jsonify({ 'result': 'ok' })

if __name__ == '__main__':
    try:
       physical_devices = tf.config.experimental.list_physical_devices('GPU')
       if len(physical_devices) > 0:
          tf.config.experimental.set_memory_growth(physical_devices[0], True)
       #yolo = YoloV3(classes=FLAGS.num_classes)
       app.run(debug=True, port=5000)
    except SystemExit:
       pass