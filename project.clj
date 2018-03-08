(defproject huzhengquan/clj-mqtt-client "0.1.1"
  :description "clojure MQTT client, based on mqtt-client(java)"
  :url "https://github.com/huzhengquan/clj-mqtt-client"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.fusesource.mqtt-client/mqtt-client "1.14"]]
  :deploy-repositories [["releases" :clojars
                         :creds :gpg]]
  :aot :all)
