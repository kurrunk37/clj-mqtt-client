(ns clj-mqtt-client.callback
  (:require [clj-mqtt-client.mqtt :as mqtt-core])
  (:import [org.fusesource.mqtt.client MQTT CallbackConnection QoS Topic Listener Callback]
           [org.fusesource.hawtbuf UTF8Buffer Buffer]
           [java.util.logging Logger Level]))

(set! *warn-on-reflection* true)

(def ^Logger logger (Logger/getLogger "mqtt-client"))

(defn- log 
  [^Level level & messages]
  (.log logger level (str messages)))

(defmacro try+
  [& body]
  `(try 
     (do ~@body)
     (catch Exception e#
       (.printStackTrace e#))))

(defn ^CallbackConnection connect
  [& {:keys [listener-on-connected
             listener-on-publish
             listener-on-disconnected
             listener-on-failure
             connect-on-success
             connect-on-failure ]
      :or {listener-on-connected (fn [] (log Level/INFO "mqtt listener connected"))
           listener-on-publish (fn [^String topic ^bytes payload] (log Level/INFO "mqtt listener new message: " topic :payload-size (count payload)))
           listener-on-disconnected (fn [] (log Level/WARNING "mqtt listener disconnected"))
           listener-on-failure (fn [^Throwable e] (.printStackTrace e))
           connect-on-success (fn [_] (log Level/INFO "mqtt connect success"))
           connect-on-failure (fn [^Throwable e] (.printStackTrace e))} 
      :as opts}]
  (log Level/INFO "mqtt begin connect" opts)
  (try+
    (let [mqtt (mqtt-core/create opts)]
      (doto ^CallbackConnection (.callbackConnection ^MQTT mqtt)
        (.listener
          (reify Listener
            (^void onConnected [this] (try+ (listener-on-connected )))
            (^void onDisconnected [this] (try+ (listener-on-disconnected )))
            (^void onFailure [this ^Throwable e] (try+ (listener-on-failure e)))
            (^void onPublish [this ^UTF8Buffer buffer-topic ^Buffer buffer-payload ^Runnable ack]
              (try+
                (do
                  (listener-on-publish (.toString (.utf8 buffer-topic)) (.toByteArray buffer-payload))
                  (.run ack))))))
        (.connect 
          (reify Callback
            (^void onSuccess [this v] (try+ (connect-on-success v)))
            (^void onFailure [this ^Throwable e] (try+ (connect-on-failure e)))))))))

(defn subscribe
  [^CallbackConnection connection topics
   & {:keys [on-success on-failure ]
      :or {on-success (fn [_] (log Level/INFO "mqtt subscribe success"))
           on-failure (fn [^Throwable e] (.printStackTrace e))}}]
  (try+
    (.subscribe 
      connection 
      (into-array Topic (for [[^String topic ^long qos] topics] (new Topic topic ^QoS (mqtt-core/long->qos qos))))
      (reify Callback
        (^void onSuccess [this qoses] (try+ (on-success qoses)))
        (^void onFailure [this ^Throwable e] (try+ (on-failure e)))))))

(defn publish
  [^CallbackConnection connection ^String topic ^bytes payload
   & {:keys [^long qos ^Boolean retain on-success on-failure]
      :or {^long qos 1
           ^Boolean retain false
           on-success (fn [_] (log Level/INFO "mqtt publish success" ))
           on-failure (fn [^Throwable e] (.printStackTrace e))}}]
  (try+
    (.publish 
      connection
      topic
      payload
      ^QoS (mqtt-core/long->qos qos)
      retain
      (reify Callback
        (^void onSuccess [this qoses] (try+ (on-success qoses)))
        (^void onFailure [this ^Throwable e] (try+ (on-failure e) ))))))

(defn disconnect
  [^CallbackConnection connection
   & {:keys [on-success on-failure]
      :or {on-success (fn [_] (log Level/INFO "mqtt disconnect success" ))
           on-failure (fn [^Throwable e] (.printStackTrace e))}}]
  (try+
    (.disconnect
      connection
      (reify Callback
        (^void onSuccess [this v] (try+ (on-success v)))
        (^void onFailure [this ^Throwable e] (try+ (on-failure e) ))))))

(defn suspend
  [^CallbackConnection connection]
  (.suspend connection))

(defn resume
  [^CallbackConnection connection]
  (.resume connection))

(defn unsubscribe
  [^CallbackConnection connection topics
   & {:keys [on-success on-failure]
      :or {on-success (fn [_] (log Level/INFO "mqtt unsubscribe success" ))
           on-failure (fn [^Throwable e] (.printStackTrace e))}}]
  (try+
    (.unsubscribe
      connection
      (into-array UTF8Buffer (for [^String topic topics] (Buffer/utf8 topic)))
      (reify Callback
        (^void onSuccess [this v] (try+ (on-success v)))
        (^void onFailure [this ^Throwable e] (try+ (on-failure e) ))))))

(defn ^Throwable failure
  [^CallbackConnection connection]
  (.failure connection))
  

