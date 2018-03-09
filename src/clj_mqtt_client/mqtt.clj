(ns clj-mqtt-client.mqtt
  (:import [org.fusesource.mqtt.client MQTT CallbackConnection QoS Topic Listener Callback]
           [java.util.logging Logger Level]))

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
        :host (.setHost mqtt ^String (first v) ^int (second v))
        :clean-session (.setCleanSession mqtt ^boolean v)
        :client-id (.setClientId mqtt ^String v)
        :keep-alive (.setKeepAlive mqtt ^short v)
        :password (.setPassword mqtt ^String v)
        :user-name (.setUserName mqtt ^String v)
        :version (.setVersion mqtt ^String v)
        :will-message (.setWillMessage mqtt ^String v)
        :will-qos (.setWillQos mqtt (long->qos v))
        :will-retain (.setWillRetain mqtt ^boolean v)
        :will-topic (.setWillTopic mqtt ^String v)
        :max-read-rate (.setMaxReadRate mqtt ^int v)
        :max-write-rate (.setMaxWriteRate mqtt ^int v)
        :receive-buffer-size (.setReceiveBufferSize mqtt ^int v)
        :send-buffer-size (.setSendBufferSize mqtt ^int v)
        :ssl-context (.setSslContext mqtt v)
        :traffic-class (.setTrafficClass mqtt v)
        :use-local-host (.setUseLocalHost mqtt ^boolean v)
        :connect-attempts-max (.setConnectAttemptsMax mqtt v)
        :reconnect-attempts-max (.setReconnectAttemptsMax mqtt v)
        :reconnect-back-off-multiplier (.setReconnectBackOffMultiplier mqtt v)
        :reconnect-delay (.setReconnectDelay mqtt v)
        :reconnect-delay-max (.setReconnectDelayMax mqtt v)
        nil))
    mqtt))
