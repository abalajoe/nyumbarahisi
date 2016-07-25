(ns ussd.httpclient
  ^{:author "jabala"
    :doc "HTTP Client"
    :added "1.0"
    }
  (:require [org.httpkit.client :as http]
            [ussd.utils.config :as config]
            [clojure.data.json :as json]))

(defn register-subscriber
  "Function sends request to subscriber registration service to register subscriber"
  [username password msisdn data-source sub-info]
  (let [options {:query-params  {:username username :password password :msisdn msisdn
                                 :data-source data-source :sub-info sub-info}
                 :body-encoding "UTF-8"
                 :headers       {"Content-type" "text/plain"}}
        request @(http/post config/register-subscriber-url options)]
    (if (:error request)
      (do (println "error: " (:error request)))
      (do (:body request)))))


#_(register-subscriber "sikika" "sikika123" "0718793456" "affiliate"
                       (json/json-str (assoc {} :gender "1" :religion "2" :age "2" :location "4")))

(defn validate-msisdn
  "Function sends request to subscriber registration service to check if msisdn exists"
  [username password msisdn]
  (let [options {:query-params  {:username username :password password :msisdn msisdn}
                 :body-encoding "UTF-8"
                 :headers       {"Content-type" "text/plain"}}
        request @(http/post config/validate-msisdn-url options)]
    (if (:error request)
      (do (println "error: " (:error request)))
      (do
        (:body request)))))

(defn get-msisdn-campaigns
  "Function sends request to subscriber registration service"
  [msisdn]
  (let [options {:body (json/write-str {:msisdn msisdn})
                 :body-encoding "UTF-8"
                 :headers       {"Content-type" "application/json"}}
        request @(http/post config/url options)]
    (if (:error request)
      (do (:error request))
      (do
        (let [resp (:body request)
              result (json/read-str resp)]
          (clojure.walk/keywordize-keys result))))))

(defn check-msisdn
  "validate msisdn"
  [username password msisdn]
  (let [options {:query-params {:username username :password password :msisdn msisdn}
                 :body-encoding "UTF-8"
                 :headers       {"Content-type" "application/json"}}
        request @(http/post config/check-msisdn-url options)]
    (if (:error request)
      (do (:error request))
      (do
        (:body request)))))

#_(type (:status-msg (clojure.walk/keywordize-keys
                       (json/read-str (validate-msisdn "sikika" "sikika123" 722123456)))))
;config/register-subscriber-url

(defn elasticsearch-ussd
  "Function sends request to elasticsearch service.
   Request comprises of subscriber demographic data."
  [username password msisdn gender age location county status]
  (let [options {:query-params  {:username username :password password :msisdn msisdn
                                 :gender gender :age age :location location :county county :status status}
                 :body-encoding "UTF-8"
                 :headers       {"Content-type" "text/plain"}}
        request @(http/post config/elasticsearch-url options)]
    (if (:error request)
      (do (println "error: " (:error request)))
      (do
        (:body request)))))

#_(type (:status-msg (clojure.walk/keywordize-keys
                       (json/read-str (validate-msisdn "sikika" "sikika123" 722123456)))))
;config/register-subscriber-url

(defn generate-target-list
  "Function sends request to campaign management for target list generation"
  [msisdn gender age location county occupation]
  (let [options {:body (json/write-str (assoc {} :msisdn msisdn
                                                 :gender gender :age age :location location :county county :occupation occupation))
                 :body-encoding "UTF-8"
                 :headers       {"Content-type" "application/json"}}
        request @(http/post config/target-list-url options)]
    (if (:error request)
      (do (println "error: " (:error request)))
      (do
        (:body request)))))

;(generate-target-list 717729123 "male" 22 "nairobi" "nairobi" "student")
