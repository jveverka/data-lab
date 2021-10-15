#!/usr/bin/env python3

import time
import sys
from absl import app, flags, logging
from absl.flags import FLAGS
import numpy as np
import tensorflow as tf
from yolov3_tf2.models import YoloV3
from yolov3_tf2.dataset import transform_images
from http import server
import logging
import socketserver
import json

logging.basicConfig(format='%(levelname)s: %(message)s', level=logging.INFO)
flags.DEFINE_string('classes', './data/coco.names', 'path to classes file')
flags.DEFINE_string('weights', './checkpoints/yolov3.tf', 'path to weights file')
flags.DEFINE_integer('size', 416, 'resize images to')
flags.DEFINE_integer('num_classes', 80, 'number of classes in the model')
global yolo

class StreamingHandler(server.BaseHTTPRequestHandler):
    def do_GET(self):
        logging.debug("GET")
        if self.path == '/version':
           version = { "version": "1.0.0" }
           content = json.dumps(version).encode('utf-8')
           self.send_response(200)
           self.send_header('Content-Type', 'application/json')
           self.send_header('Content-Length', len(content))
           self.end_headers()
           self.wfile.write(content)
        else:
           self.send_error(404)
           self.end_headers()
    def do_POST(self):
        logging.debug("POST")
        if self.path == '/upload-detect':
           file_length = int(self.headers['Content-Length'])
           logging.info("POST /upload-detect: %s", str(file_length))
           file_data = self.rfile.read(file_length)
           result = evaluateImage(file_data, "/upload-detect")
           content = json.dumps(result).encode('utf-8')
           self.send_response(200)
           self.send_header('Content-Type', 'application/json')
           self.send_header('Content-Length', len(content))
           self.end_headers()
           self.wfile.write(content)
        elif self.path == '/local-detect':
           content_len = int(self.headers.get('content-length'))
           post_body = self.rfile.read(content_len)
           body_data = json.loads(post_body)
           file_path = body_data['path']
           logging.info("POST /local-detect: %s", str(file_path))
           file_data = open(file_path, 'rb').read()
           result = evaluateImage(file_data, file_path)
           content = json.dumps(result).encode('utf-8')
           self.send_response(200)
           self.send_header('Content-Type', 'application/json')
           self.send_header('Content-Length', len(content))
           self.end_headers()
           self.wfile.write(content)
        else:
            self.send_error(404)
            self.end_headers()

class StreamingServer(socketserver.ThreadingMixIn, server.HTTPServer):
    allow_reuse_address = True
    daemon_threads = True


def evaluateImage(content, path):
    img = tf.image.decode_image(content, channels=3)
    img = tf.expand_dims(img, 0)
    img = transform_images(img, FLAGS.size)

    t1 = time.time()
    boxes, scores, classes, nums = yolo(img)
    t2 = time.time() - t1
    logging.info('time: {}'.format(t2))

    objects = list(range(nums[0]))
    for i in range(nums[0]):
        object = {
            "classId": class_names[int(classes[0][i])],
            "score": np.array(scores[0][i]).tolist(),
            "box": np.array(boxes[0][i]).tolist()
        }
        objects[i] = object
    result = { 'result': True, 'path': path, 'time': t2, 'objects': objects, 'message': 'OK' }
    logging.info('detections: ' + str(result))
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

        address = ("0.0.0.0", 5000)
        logging.info("starting server at: %s", str(address))
        server = StreamingServer(address, StreamingHandler)
        server.serve_forever()
    except (KeyboardInterrupt):
        pass
