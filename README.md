# clj-mqtt-client

Clojure MQTT client, based on [mqtt-client](https://github.com/fusesource/mqtt-client), Applications can use a blocking API style, a futures based API, or a callback/continuations passing API style.

## Installation

Leiningen dependency information:

```clojure
[huzhengquan/clj-mqtt-client "0.1.7"]
```

## Usage

### Using the Blocking API

```clojure
(require '[clj-mqtt-client.blocking :as mqtt])

(let [conn (mqtt/connect :uri "tcp://0.0.0.0:1886"
                         :user-name "xxx"
                         :password "xxx"
                         :client-id "xxx")]
  ; publish message
  (mqtt/publish conn "topic" (.getBytes "payload" "UTF-8"))
  ; publish opts
  (mqtt/publish conn "topic" (.getBytes "payload") :qos 1 :retain false)
  ; subscribe
  (mqtt/subscribe conn [["topic1" 1] ["topic2" 2]])
  ; receive
  (loop []
    (let [{:keys [topic payload]} (mqtt/receive conn)]
      ; topic ^String "topic1"
      ; payload ^bytes xxx 
      (println topic (new String payload "utf-8"))
      )
    (recur))
  ; disconnect
  (mqtt/disconnect conn)
  ; unsubscribe
  (mqtt/unsubscribe conn ["topic1" "topic2"])
  ; connected?
  (mqtt/connected? conn)
  )
```

### Using the Callback/Continuation Passing based API

```clojure
(require '[clj-mqtt-client.callback :as mqtt])

; create connection
(let [conn (mqtt/connect :uri "tcp://0.0.0.0:1886"
                         :user-name "xxx"
                         :password "xxx"
                         :client-id "xxx"
                         :listener-on-publish (fn [^String topic ^bytes payload] 
                                                ; new message
                                                )
                         )]
  ; publish message
  (mqtt/publish conn "topic" (.getBytes "payload" "UTF-8"))
  ; publish opts
  (mqtt/publish conn "topic" (.getBytes "payload") :qos 1 :retain false)
  ; subscribe
  (mqtt/subscribe conn [["topic" 1] ["topic2" 2]])
  ; disconnect
  (mqtt/disconnect conn)
  )
```

### Using the Future based API

```clojure
(require '[clj-mqtt-client.future :as mqtt])

; create connection
(let [conn (mqtt/future-connection :uri "tcp://0.0.0.0:1886" ...)]

  ; connect
  (let [f (mqtt/connect conn)]
    (.await f))
  ; publish
  (let [f (mqtt/publish conn "topic" (.getBytes "payload"))]
    (.await f))
  ; subscribe
  (let [f (mqtt/subscribe conn [["topic" 1] ["topic2" 2]])]
    (.await f))
  ; receive
  (let [f (mqtt/receive conn)
        msg (mqtt/read-message f)]
    ; ...
    )
  ; disconnect
  (let [f (mqtt/disconnect conn)]
    (.await f))
  )
```


### MQTT connect Configuration

| Options                   | Default                  | Description                                                                                       |
| ------------------------- | ------------------------ | ------------------------------------------------------------------------------------------------- |
| :uri                      | "tcp://127.0.0.1:1883"   |                                                                                                   |
| :user-name                |                          |                                                                                                   |
| :password                 |                          |                                                                                                   |
| :client-id                |                          |                                                                                                   |
| :clean-session            | true                     |                                                                                                   |
| :keep-alive               | 30                       | Configures the Keep Alive timer in seconds.                                                       |
| :version                  | "3.1"                    | Set to "3.1.1" to use MQTT version 3.1.1.                                                         |
| :will-message             | ""                       | The Will message to send.                                                                         |
| :will-qos                 | 0                        | Sets the quality of service to use for the Will message.                                          |
| :will-topic               |                          |                                                                                                   |
| :will-retain              |                          | Set to true if you want the Will to be published with the retain option.                          |
| :receive-buffer-size      | 65536 (64k)              | Sets the size of the internal socket receive buffer.                                              |
| :send-buffer-size         | 65536 (64k)              | Sets the size of the internal socket send buffer.                                                 |
| :traffic-class            | 8                        | Sets traffic class or type-of-service octet in the IP header for packets sent from the transport. |
| :use-local-host           | true                     |                                                                                                   |
| :local-address            |                          |                                                                                                   |
| :reconnect-delay          | 10                       | How long to wait in ms before the first reconnect attempt.                                        |
| :reconnect-delay-max      | 30000                    | The maximum amount of time in ms to wait between reconnect attempts.                              |
| :reconnect-attempts-max   | -1                       |                                                                                                   |
| :connect-attempts-max     | -1                       |                                                                                                   |
| :max-read-rate            | 0 (disable)              | Sets the maximum bytes per second that this transport will receive data at.                       |
| :max-write-rate           | 0 (disable)              | Sets the maximum bytes per second that this transport will send data at.                          |

more information : [mqtt-client](https://github.com/fusesource/mqtt-client)

## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
