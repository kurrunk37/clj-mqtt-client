(ns clj-mqtt-client.mqtt
  (:import [org.fusesource.mqtt.client MQTT QoS Topic Message]
           [java.util.logging Logger Level]))

(set! *warn-on-reflection* true)

(defn ^QoS long->qos
  ([^long long-qos] (long->qos long-qos QoS/AT_MOST_ONCE))
  ([^long long-qos ^QoS default-qos]
   (case long-qos
     0 QoS/AT_MOST_ONCE
     1 QoS/AT_LEAST_ONCE
     2 QoS/EXACTLY_ONCE
     default-qos)))

(defn ^MQTT create
  [opts]
  (let [mqtt (new MQTT)]
    (doseq [[k v] opts]
      (case k
        :uri (.setHost mqtt ^String v)
        :host (.setHost mqtt ^String (first v) ^Integer (second v))
        :clean-session (.setCleanSession mqtt ^Boolean v)
        :client-id (.setClientId mqtt ^String v)
        :keep-alive (.setKeepAlive mqtt ^Short v)
        :password (.setPassword mqtt ^String v)
        :user-name (.setUserName mqtt ^String v)
        :version (.setVersion mqtt ^String v)
        :will-message (.setWillMessage mqtt ^String v)
        :will-qos (.setWillQos mqtt (long->qos v))
        :will-retain (.setWillRetain mqtt ^Boolean v)
        :will-topic (.setWillTopic mqtt ^String v)
        :max-read-rate (.setMaxReadRate mqtt ^Integer v)
        :max-write-rate (.setMaxWriteRate mqtt ^Integer v)
        :receive-buffer-size (.setReceiveBufferSize mqtt ^Integer v)
        :send-buffer-size (.setSendBufferSize mqtt ^Integer v)
        :ssl-context (.setSslContext mqtt ^javax.net.ssl.SSLContext v)
        :traffic-class (.setTrafficClass mqtt ^Integer v)
        :use-local-host (.setUseLocalHost mqtt ^Boolean v)
        :local-address (.setLocalAddress mqtt ^String v)
        :connect-attempts-max (.setConnectAttemptsMax mqtt ^long v)
        :reconnect-attempts-max (.setReconnectAttemptsMax mqtt ^long v)
        :reconnect-back-off-multiplier (.setReconnectBackOffMultiplier mqtt ^double v)
        :reconnect-delay (.setReconnectDelay mqtt ^long v)
        :reconnect-delay-max (.setReconnectDelayMax mqtt ^long v)
        nil))
    mqtt))

(defn msg->coll 
  [^Message msg & {:keys [fields] :or {fields [:topic :payload]}}]
  (into {} (map (fn [k]
                  {k (case k
                       :topic (.getTopic msg)
                       :payload (.getPayload msg)
                       nil
                       ) })
                fields)))

