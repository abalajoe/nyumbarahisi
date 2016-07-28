(ns ussd.httpserver
  ^{:author "jabala"
    :doc "HTTP Server"
    :added "1.0"}
  (:require [clojure.tools.logging :as log]
            [ussd.utils.config :as config]
            [ussd.utils.menu :as menu]
            [ussd.session :as session]
            [ussd.utils.util :as util]
            [clojure.data.json :as json]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ussd.httpclient :as client]
            [ussd.model.datasource :as db])
  (:use org.httpkit.server
        [compojure.route :only [not-found]]
        [compojure.handler :only [site]] ; form, query params decode; cookie; session, etc
        [compojure.core :only [defroutes GET POST DELETE ANY context]]))

(defn initialize-http
  "Function defines server once"
  []
  (defonce server (atom nil))
  (log/info "initializing http server ..."))

(defn invalid-choice-menu
  "Navigate user to same menu"
  [sessionId menu start end]
  (session/modify-menu sessionId start end)
  menu)

(defn main-menu
  "Navigate user to main menu"
  [sessionId]
  (session/reset-session sessionId)  ;; reset session
  (menu/main-menu))                  ;; return main menu

(defn exit-menu
  "Exit service"
  [sessionId]
  (session/clear-session sessionId)    ;; remove session ID from session
  (menu/exit-menu))                    ;; exit message

(defn validate-msisdn
  "Validate YOB entered by subscriber.
  Also register. "
  [sessionId seventh-input]
  (println sessionId)
  (let [msisdn (try
               (Integer/parseInt seventh-input)
               (catch Exception e
                 0))]
    (cond
      (= 0 msisdn) (invalid-choice-menu sessionId (menu/reset-msisdn-menu) 0 6) ;; invalid year return user to same menu
      (>= 9 (count (clojure.string/trim seventh-input)))(invalid-choice-menu sessionId (menu/reset-msisdn-menu) 0 6)
      (<= 13 (count (clojure.string/trim seventh-input)))(invalid-choice-menu sessionId (menu/reset-msisdn-menu) 0 6)
      :else (do
              (println "[] " sessionId (session/get-session-data sessionId) msisdn)
              (util/parse-res sessionId (session/get-session-data sessionId))
              )))) ;; register user

(defn process-msisdn
  "process age and register"
  [sessionId]
  (let [seventh-input (get (session/get-session-data sessionId) 6)]
    (case seventh-input
      nil (menu/msisdn-menu)                ;; age menu
      (do
        (validate-msisdn sessionId seventh-input)))))

(defn validate-cost
  "Validate YOB entered by subscriber.
  Also register. "
  [sessionId sixth-input]
  (println sessionId)
  (let [cost (try
                     (Integer/parseInt sixth-input)
                     (catch Exception e
                       0))]
    (cond
      (= 0 cost) (invalid-choice-menu sessionId (menu/reset-cost-menu) 0 5) ;; invalid year return user to same menu
      (>= 3 (count (clojure.string/trim sixth-input)))(invalid-choice-menu sessionId (menu/reset-cost-menu) 0 5)
      :else (do
              (process-msisdn sessionId))))) ;; register user

(defn process-cost
  "process age and register"
  [sessionId sixth-input]
  (case sixth-input
    nil (menu/cost-menu)                ;; age menu
    (do
      (validate-cost sessionId sixth-input)))) ;; check whether YOB is valid

(defn process-age-register
  "process age and register"
  [sessionId fifth-input msisdn]
  (case fifth-input
    nil (menu/plot-menu)                ;; age menu
    (do
      (validate-cost sessionId fifth-input)))) ;; check whether YOB is valid

(defn back-menu
  "navigate user to previous menu"
  [sessionId menu start end]
  (session/modify-menu sessionId start end)
  menu)

(defn process-region
    "navigate user to previous menu"
    [fourth-input sessionId fifth-input]
  (println "## [" fourth-input "][" sessionId "][" fifth-input"]")
  (case fourth-input
    nil (do (println "@ " )(menu/region-menu))
    (do (println "><")
        (case fifth-input
          ("1" "2" "3" "4" "5") (do
                                  (let [sixth-input (get (session/get-session-data sessionId) 5)]
                                    (process-cost sessionId sixth-input)))
          nil (menu/plot-menu)
          "00" (main-menu sessionId)
          "#" (exit-menu sessionId)
          "98" (back-menu sessionId (menu/nairobi-county-menu) 0 3)
          (invalid-choice-menu sessionId (menu/reset-plot-menu) 0 4)))))

(defn get-data
  "navigate user to previous menu"
  [sessionId]
  (let [levels (session/get-session-data sessionId)
        data (util/parse-data levels)
        k (atom 0)]
    (doseq [x data]
      (println (swap! k inc)". " (:cost x) " - " (:mobile x)))))

(defn ussd-handler
  "Function displays menu"
  [req]
  (println "++ " req)
  (let [sessionId (-> req  :params :sessionId)              ;; get session ID
        serviceCode (-> req  :params :serviceCode)          ;; get service code
        phone-number (-> req  :params :phoneNumber)               ;; get phone number
        msisdn (subs phone-number 4)
        text (-> req  :params :text)]                       ;; get text
          ;(or "empty" "inactive")
          (let []
            (log/info "register subscriber")
            (session/start-session sessionId)               ;; start session
            (cond
              (empty? text) (do (println "1") (menu/main-menu)) ;; display main menu
              :else
              (do
                (let [split-input (clojure.string/split text #"\*") ;; split the text
                      last-input (last split-input)         ;; get the current user input
                      _ (session/update-session sessionId last-input) ;; append new user input to previous user input journey
                      session (session/get-session-data sessionId) ;; get the current user input journey, vector
                      first-input (get session 0)           ;; get the first input
                      second-input (get session 1)          ;; get the second input
                      third-input (get session 2)           ;; get the third input
                      fourth-input (get session 3)          ;; get the fourth input
                      fifth-input (get session 4)           ;; get the fifth input
                      ]
                  (case first-input
                    "1" (do
                            (condp = second-input
                                  "1" (do
                                        (condp = third-input
                                          "1" (do
                                                (process-region fourth-input sessionId fifth-input))
                                          nil (menu/nairobi-county-menu)
                                          "00" (main-menu sessionId)
                                          "#" (exit-menu sessionId)
                                          "98" (back-menu sessionId (menu/location-menu) 0 1)
                                          (invalid-choice-menu sessionId (menu/reset-nairobi-county-menu) 0 2)))
                                  "2" (do
                                        (cond
                                          (not (= -1 (.indexOf ["1" "2" "3" "4" "5" "6"] third-input)))
                                            (do (process-region fourth-input sessionId fifth-input))
                                          (= nil fourth-input) (menu/coast-county-menu)
                                          (= "00" fourth-input) (main-menu sessionId)
                                          (= "#" fourth-input) (exit-menu sessionId)
                                          (= "98" fourth-input) (back-menu sessionId (menu/location-menu) 0 1)
                                          :else (invalid-choice-menu sessionId (menu/reset-coast-county-menu) 0 2)))
                                  "3" (do
                                        (println " = [" session "] = [" third-input "] = ")
                                        (cond
                                          (not (= -1 (.indexOf ["1" "2" "3"] third-input))) (do (println "hello " third-input) (process-region fourth-input sessionId fifth-input))
                                          (= nil third-input) (menu/north-eastern-county-menu)
                                          (= "00" third-input) (main-menu sessionId)
                                          (= "#" third-input) (exit-menu sessionId)
                                          (= "98" third-input) (back-menu sessionId (menu/location-menu) 0 1)
                                          :else (invalid-choice-menu sessionId (menu/reset-north-eastern-county-menu) 0 2)))
                                  "4" (do
                                        (cond
                                          (not (= -1 (.indexOf ["1" "2" "3" "4" "5" "6" "7" "8"] fourth-input)))
                                            (do (process-region fourth-input sessionId fifth-input))
                                          (= nil fourth-input) (menu/eastern-county-menu)
                                          (= "00" fourth-input) (main-menu sessionId)
                                          (= "#" fourth-input) (exit-menu sessionId)
                                          (= "98" fourth-input) (back-menu sessionId (menu/location-menu) 0 1)
                                          :else (invalid-choice-menu sessionId (menu/reset-eastern-county-menu) 0 2)))
                                  "5" (do
                                        (cond
                                          (not (= -1 (.indexOf ["1" "2" "3" "4" "5"] fourth-input)))
                                            (do (process-region fourth-input sessionId fifth-input))
                                          (= nil fourth-input) (menu/central-county-menu)
                                          (= "00" fourth-input) (main-menu sessionId)
                                          (= "#" fourth-input) (exit-menu sessionId)
                                          (= "98" fourth-input) (back-menu sessionId (menu/location-menu) 0 1)
                                          :else (invalid-choice-menu sessionId (menu/reset-central-county-menu) 0 2)))
                                  "6" (do
                                        (cond
                                          (not (= -1 (.indexOf ["1" "2" "3" "4" "5" "6" "7" "8" "9" "10" "11" "12" "13" "14"] fourth-input)))
                                            (do (process-region fourth-input sessionId fifth-input))
                                          (= nil fourth-input) (menu/riftvalley-county-menu)
                                          (= "00" fourth-input) (main-menu sessionId)
                                          (= "#" fourth-input) (exit-menu sessionId)
                                          (= "98" fourth-input) (back-menu sessionId (menu/location-menu) 0 1)
                                          :else (invalid-choice-menu sessionId (menu/reset-riftvalley-county-menu) 0 2)))
                                  "7" (do
                                        (cond
                                          (not (= -1 (.indexOf ["1" "2" "3" "4"] fourth-input)))
                                            (do (process-region fourth-input sessionId fifth-input))
                                          (= nil fourth-input) (menu/western-county-menu)
                                          (= "00" fourth-input) (main-menu sessionId)
                                          (= "#" fourth-input) (exit-menu sessionId)
                                          (= "98" fourth-input) (back-menu sessionId (menu/location-menu) 0 1)
                                          :else (invalid-choice-menu sessionId (menu/reset-western-county-menu) 0 2)))
                                  "8" (do
                                        (cond
                                          (not (= -1 (.indexOf ["1" "2" "3" "4"] fourth-input)))
                                            (do (process-region fourth-input sessionId fifth-input))
                                          (= nil fourth-input) (menu/nyanza-county-menu)
                                          (= "00" fourth-input) (main-menu sessionId)
                                          (= "#" fourth-input) (exit-menu sessionId)
                                          (= "98" fourth-input) (back-menu sessionId (menu/location-menu) 0 1)
                                          :else (invalid-choice-menu sessionId (menu/reset-nyanza-county-menu) 0 2)))
                                  nil (menu/location-menu)
                                  "00" (main-menu sessionId)
                                  "#" (exit-menu sessionId)
                                  "98" (back-menu sessionId (menu/main-menu) 0 0)
                                  (invalid-choice-menu sessionId (menu/reset-location-menu) 0 1)))
                    "2" (do
                          (condp = second-input
                            "1" (do
                                  (condp = third-input
                                    "1" (do
                                          (case fourth-input
                                            ("1" "2" "3" "4" "5") (do
                                                                    (get-data sessionId))
                                            nil (menu/plot-menu)
                                            "00" (main-menu sessionId)
                                            "#" (exit-menu sessionId)
                                            "98" (back-menu sessionId (menu/nairobi-county-menu) 0 2)
                                            (invalid-choice-menu sessionId (menu/reset-plot-menu) 0 3)))
                                    nil (menu/nairobi-county-menu)
                                    "00" (main-menu sessionId)
                                    "#" (exit-menu sessionId)
                                    "98" (back-menu sessionId (menu/location-menu) 0 1)
                                    (invalid-choice-menu sessionId (menu/reset-nairobi-county-menu) 0 2)))
                            "2" (do
                                  (cond
                                    (not (= -1 (.indexOf ["1" "2" "3" "4" "5" "6"] fourth-input))) (do (process-age-register sessionId fifth-input msisdn))
                                    (= nil fourth-input) (menu/coast-county-menu)
                                    (= "00" fourth-input) (main-menu sessionId)
                                    (= "#" fourth-input) (exit-menu sessionId)
                                    (= "98" fourth-input) (back-menu sessionId (menu/location-menu) 0 1)
                                    :else (invalid-choice-menu sessionId (menu/reset-coast-county-menu) 0 2)))
                            "3" (do
                                  (cond
                                    (not (= -1 (.indexOf ["1" "2" "3"] fourth-input))) (do (process-age-register sessionId fifth-input msisdn))
                                    (= nil fourth-input) (menu/north-eastern-county-menu)
                                    (= "00" fourth-input) (main-menu sessionId)
                                    (= "#" fourth-input) (exit-menu sessionId)
                                    (= "98" fourth-input) (back-menu sessionId (menu/location-menu) 0 1)
                                    :else (invalid-choice-menu sessionId (menu/reset-north-eastern-county-menu) 0 2)))
                            "4" (do
                                  (cond
                                    (not (= -1 (.indexOf ["1" "2" "3" "4" "5" "6" "7" "8"] fourth-input))) (do (process-age-register sessionId fifth-input msisdn))
                                    (= nil fourth-input) (menu/eastern-county-menu)
                                    (= "00" fourth-input) (main-menu sessionId)
                                    (= "#" fourth-input) (exit-menu sessionId)
                                    (= "98" fourth-input) (back-menu sessionId (menu/location-menu) 0 1)
                                    :else (invalid-choice-menu sessionId (menu/reset-eastern-county-menu) 0 2)))
                            "5" (do
                                  (cond
                                    (not (= -1 (.indexOf ["1" "2" "3" "4" "5"] fourth-input))) (do (process-age-register sessionId fifth-input msisdn))
                                    (= nil fourth-input) (menu/central-county-menu)
                                    (= "00" fourth-input) (main-menu sessionId)
                                    (= "#" fourth-input) (exit-menu sessionId)
                                    (= "98" fourth-input) (back-menu sessionId (menu/location-menu) 0 1)
                                    :else (invalid-choice-menu sessionId (menu/reset-central-county-menu) 0 2)))
                            "6" (do
                                  (cond
                                    (not (= -1 (.indexOf ["1" "2" "3" "4" "5" "6" "7" "8" "9" "10" "11" "12" "13" "14"] fourth-input))) (do (process-age-register sessionId fifth-input msisdn))
                                    (= nil fourth-input) (menu/riftvalley-county-menu)
                                    (= "00" fourth-input) (main-menu sessionId)
                                    (= "#" fourth-input) (exit-menu sessionId)
                                    (= "98" fourth-input) (back-menu sessionId (menu/location-menu) 0 1)
                                    :else (invalid-choice-menu sessionId (menu/reset-riftvalley-county-menu) 0 2)))
                            "7" (do
                                  (cond
                                    (not (= -1 (.indexOf ["1" "2" "3" "4"] fourth-input))) (do (process-age-register sessionId fifth-input msisdn))
                                    (= nil fourth-input) (menu/western-county-menu)
                                    (= "00" fourth-input) (main-menu sessionId)
                                    (= "#" fourth-input) (exit-menu sessionId)
                                    (= "98" fourth-input) (back-menu sessionId (menu/location-menu) 0 1)
                                    :else (invalid-choice-menu sessionId (menu/reset-western-county-menu) 0 2)))
                            "8" (do
                                  (cond
                                    (not (= -1 (.indexOf ["1" "2" "3" "4"] fourth-input))) (do (process-age-register sessionId fifth-input msisdn))
                                    (= nil fourth-input) (menu/nyanza-county-menu)
                                    (= "00" fourth-input) (main-menu sessionId)
                                    (= "#" fourth-input) (exit-menu sessionId)
                                    (= "98" fourth-input) (back-menu sessionId (menu/location-menu) 0 1)
                                    :else (invalid-choice-menu sessionId (menu/reset-nyanza-county-menu) 0 2)))
                            nil (menu/location-menu)
                            "00" (main-menu sessionId)
                            "#" (exit-menu sessionId)
                            "98" (back-menu sessionId (menu/main-menu) 0 0)
                            (invalid-choice-menu sessionId (menu/reset-location-menu) 0 1)))
                    nil (do (println "main menu")(menu/main-menu))
                    "#" (do (println "exit menu") (exit-menu sessionId))               ;; exit service
                    (do (println "invalid") (invalid-choice-menu sessionId (menu/reset-main-menu) 0 0)))))))))

;; routes
(defroutes all-routes
           (POST "/mode/sikika/ussd/" [] ussd-handler)
           (not-found "invalid request")) ;; return 404

(def app
  (wrap-defaults all-routes (assoc-in site-defaults [:security :anti-forgery] false)))


(defn start-server
  "Function starts server"
  []
  (try
    (do
      (reset! server (run-server (site #'all-routes) {:port config/server-port}))
      (log/info "server started at port " config/server-port))
    (catch Exception e
      (do
        (throw (Exception. (str "Error: port " config/server-port " is already in use. " e)))))))

(defn stop-server
  "Function gracefuly shuts down server"
  []
  (when-not (nil? @server)
    (@server :timeout 100) ;; wait 100ms for existing requests to be finished
    (reset! server nil)
    (log/info "server shut down")))


