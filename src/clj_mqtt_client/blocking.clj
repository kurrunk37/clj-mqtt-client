(ns clj-mqtt-client.blocking
  (:require [clj-mqtt-client.mqtt :as mqtt-core])
  (:import [org.fusesource.mqtt.client MQTT BlockingConnection QoS Topic Message]))

(set! *warn-on-reflection* true)

(defn ^BlockingConnection connect
  [& {:as opts}]
  (let [conn (.blockingConnection ^MQTT (mqtt-core/create opts))]
    (.connect ^BlockingConnection conn)
    conn))

(defn ^bytes subscribe
  [^BlockingConnection connection topics]
  (.subscribe 
    connection 
    (into-array Topic (for [[^String topic ^long qos] topics] (new Topic topic ^QoS (mqtt-core/long->qos qos))))))

(defn publish
  [^BlockingConnection connection ^String topic ^bytes payload
   & {:keys [^long qos ^Boolean retain]
      :or {^long qos 1
           ^Boolean retain false}}]
  (.publish 
    connection
    topic
    payload
    ^QoS (mqtt-core/long->qos qos)
    retain))

(defn ^clojure.lang.IPersistentMap receive
  [^BlockingConnection connection 
   & {:keys [fields]
      :or {fields [:topic :payload] }}]
  (let [^Message message (.receive connection)]
    (.ack message)
    (mqtt-core/msg->coll message :fields fields)))

(defn disconnect
  [^BlockingConnection connection ]
  (.disconnect connection))

(defn ^boolean connected?
  [^BlockingConnection connection ]
  (.isConnected connection))

(defn unsubscribe
  [^BlockingConnection connection topics]
  (.unsubscribe connection (into-array String topics)))

(defn suspend
  [^BlockingConnection connection ]
  (.suspend connection))

(defn resume
  [^BlockingConnection connection ]
  (.resume connection))

(defn kill
  [^BlockingConnection connection ]
  (.kill connection))
