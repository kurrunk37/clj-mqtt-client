(defproject huzhengquan/clj-mqtt-client "0.1.4"
  :description "Clojure MQTT client, Applications can use a blocking API style, or a callback/continuations passing API style"
  :url "https://github.com/huzhengquan/clj-mqtt-client"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.fusesource.mqtt-client/mqtt-client "1.14"]]
  :deploy-repositories [["releases" :clojars
                         :creds :gpg]]
  :aot :all)
