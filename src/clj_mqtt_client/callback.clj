(ns clj-mqtt-client.callback
  (:require [clj-mqtt-client.mqtt :as mqtt-core])
  (:import [org.fusesource.mqtt.client MQTT CallbackConnection QoS Topic Listener Callback]
           [java.util.logging Logger Level]))

(def logger ^Logger (Logger/getLogger "mqtt-client"))

(defn- log 
  [^Level level & messages]
  (.log ^Logger logger ^Level level (str messages)))

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
           listener-on-publish (fn [^String topic _] (log Level/INFO "mqtt listener new message: " topic))
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
            (^void onPublish [this ^org.fusesource.hawtbuf.UTF8Buffer buffer-topic ^org.fusesource.hawtbuf.Buffer buffer-payload ^Runnable ack]
              (try+
                (let [topic (.toString (.utf8 buffer-topic))
                      payload (.toString (.utf8 buffer-payload))]
                  (log Level/INFO "mqtt new message:" topic (if (> (count payload) 100) (str (subs payload 0 100) "...") payload))
                  (listener-on-publish topic payload)
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
      (into-array Topic (for [[topic qos] topics] (new Topic topic (mqtt-core/long->qos qos))))
      (reify Callback
        (^void onSuccess [this qoses] (try+ (on-success qoses)))
        (^void onFailure [this ^Throwable e] (try+ (on-failure e)))))))

(defn publish
  [^CallbackConnection connection ^String topic ^String payload
   & {:keys [^long qos ^boolean retain on-success on-failure]
      :or {qos 1
           retain false
           on-success (fn [_] (log Level/INFO "mqtt publish success" ))
           on-failure (fn [^Throwable e] (.printStackTrace e))}}]
  (try+
    (.publish 
      connection
      ^String topic
      (.getBytes payload)
      ^QoS (mqtt-core/long->qos qos)
      ^boolean retain
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

