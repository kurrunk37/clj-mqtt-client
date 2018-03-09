(ns clj-mqtt-client.blocking
  (:require [clj-mqtt-client.mqtt :as mqtt-core])
  (:import [org.fusesource.mqtt.client MQTT BlockingConnection QoS Topic Message]))

(defn ^BlockingConnection connect
  [& {:as opts}]
  (let [conn (.blockingConnection ^MQTT (mqtt-core/create opts))]
    (.connect ^BlockingConnection conn)
    conn))

(defn ^bytes subscribe
  [^BlockingConnection connection topics]
  (.subscribe 
    ^BlockingConnection connection 
    (into-array Topic (for [[topic qos] topics] (new Topic topic (mqtt-core/long->qos qos))))))

(defn publish
  [^BlockingConnection connection ^String topic ^bytes payload
   & {:keys [^long qos ^boolean retain]
      :or {qos 1
           retain false}}]
  (.publish 
    ^BlockingConnection connection
    ^String topic
    ^bytes payload
    ^QoS (mqtt-core/long->qos qos)
    ^boolean retain))

(defn receive
  [^BlockingConnection connection 
   & {:keys [fields]
      :or {fields [:topic :payload] }}]
  (let [message (.receive connection)
        return (into {} (map (fn [k]
                               {k (case k
                                    :topic (.getTopic ^Message message)
                                    :payload (.getPayload ^Message message)
                                    nil
                                    ) })
                             fields))]
    (.ack ^Message message)
    return))

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
