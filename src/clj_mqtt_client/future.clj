(ns clj-mqtt-client.future
  (:require [clj-mqtt-client.mqtt :as mqtt-core])
  (:import [org.fusesource.mqtt.client MQTT FutureConnection QoS Topic Message Future]))

(set! *warn-on-reflection* true)

(defn ^FutureConnection future-connection
  [& {:as opts}]
  (.futureConnection ^MQTT (mqtt-core/create opts)))

(defn ^Future connect
  [^FutureConnection conn]
  (.connect conn))

(defn ^Future subscribe
  [^FutureConnection conn topics]
  (.subscribe 
    conn
    (into-array Topic (for [[^String topic ^long qos] topics] (new Topic topic ^QoS (mqtt-core/long->qos qos))))))

(defn ^Future publish
  [^FutureConnection conn ^String topic ^bytes payload
   & {:keys [^long qos ^Boolean retain]
      :or {^long qos 1
           ^Boolean retain false}}]
  (.publish 
    conn
    topic
    payload
    ^QoS (mqtt-core/long->qos qos)
    retain))

(defn ^Future receive
  [^FutureConnection conn]
  (.receive conn))

(defn read-message
  [^Future f & {:keys [fields] :or {fields [:topic :payload]}}]
  (let [^Message msg (.await f)]
    (.ack msg)
    (mqtt-core/msg->coll msg :fields fields)))


(defn ^Future disconnect
  [^FutureConnection conn]
  (.disconnect conn))

(defn ^boolean connected?
  [^FutureConnection conn]
  (.isConnected conn))

(defn ^Future unsubscribe
  [^FutureConnection connection topics]
  (.unsubscribe connection (into-array String topics)))


(defn resume
  [^FutureConnection connection ]
  (.resume connection))

(defn kill
  [^FutureConnection connection ]
  (.kill connection))
