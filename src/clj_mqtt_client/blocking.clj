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
  [^BlockingConnection connection ^String topic ^String payload
   & {:keys [^long qos ^boolean retain]
      :or {qos 1
           retain false}}]
  (.publish 
    ^BlockingConnection connection
    ^String topic
    (.getBytes ^String payload)
    ^QoS (mqtt-core/long->qos qos)
    ^boolean retain))

(defn receive
  [^BlockingConnection connection 
   & {:keys [fields coding]
      :or {fields [:topic :payload]
           coding "UTF-8"}}]
  (let [message (.receive connection)
        return (into {} (map (fn [k]
                               {k (case k
                                    :topic (.getTopic ^Message message)
                                    :payload (new String (.getPayload ^Message message) coding)
                                    nil
                                    ) })
                             fields))]
    (.ack ^Message message)
    return))

(defn disconnect
  [^BlockingConnection connection ]
  (.disconnect connection))
