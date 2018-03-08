# clj-mqtt-client

clojure MQTT client, callback/continuations passing API style.

## Installation

Leiningen dependency information:

```clojure
[huzhengquan/clj-mqtt-client "0.1.1"]
```

## Usage

```clojure
(require '[clj-mqtt-client.callback :as mqtt])

; create connection
(let [conn (mqtt/connect :uri "tcp://0.0.0.0:1886"
                         :user-name "xxx"
                         :password "xxx"
                         :client-id "xxx"
                         :listener-on-publish (fn [topic payload] 
                                                ; new message
                                                )
                         )]
  ; publish message
  (mqtt/publish conn "topic" "payload")
  ; subscribe
  (mqtt/subscribe conn [["topic" 1] ["topic2" 2]])
  ; disconnect
  (mqtt/disconnect conn)
  )
```

### MQTT Configuration

| Options                   | Default                  | Description                    |
| ------------------------- | ------------------------ | ------------------------------ |
| :uri                      | "tcp://127.0.0.1:1883"   |                                |
| :user-name                |                          |                                |
| :password                 |                          |                                |
| :client-id                |                          |                                |
| :keep-alive               |                          |                                |
| :receive-buffer-size      | 65536                    |                                |
| :send-buffer-size         | 65536                    |                                |
| :use-local-host           | true                     |                                |
| :local-address            |                          |                                |
| :reconnect-delay          | 10                       |                                |
| :reconnect-delay-max      | 30000                    |                                |
| :reconnect-attempts-max   | -1                       |                                |
| :connect-attempts-max     | -1                       |                                |
| :clean-session            |                          |                                |
| :will-message             |                          |                                |
| :will-qos                 |                          |                                |
| :will-topic               |                          |                                |
| :will-retain              |                          |                                |
| :version                  | "3.1"                    |                                |
| :max-read-rate            | 0                        |                                |
| :max-write-rate           | 0                        | maximum bytes per second       |

more information : [mqtt-client](https://github.com/fusesource/mqtt-client)

## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
