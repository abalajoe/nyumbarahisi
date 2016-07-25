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
          [ussd.httpclient :as client])
  (:use org.httpkit.server
        [compojure.route :only [not-found]]
        [compojure.handler :only [site]] ; form, query params decode; cookie; session, etc
        [compojure.core :only [defroutes GET POST DELETE ANY context]])
  (import com.mode.sikika.SikikaVoiceCallService))

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

(defn register-subscriber
  "Register subscriber"
  [sessionId msisdn]
  (println "[] " sessionId (session/get-session-data sessionId) msisdn)
  (util/parse-res sessionId (session/get-session-data sessionId) msisdn)
  #_(case seventh-input
    "1" (do
          (util/parse-res sessionId (session/get-session-data sessionId) msisdn)) ;; register subscriber
    nil (menu/register-menu)                                ;; register menu
    "00" (main-menu sessionId)                              ;; main menu
    "#" (exit-menu sessionId)                               ;; exit menu
    (do                                                     ;; invalid choice
      (session/modify-menu sessionId 0 6)
      (menu/reset-register-menu))))                         ;; register menu

(defn validate-year
  "Validate YOB entered by subscriber.
  Also register. "
  [sessionId fifth-input msisdn]
  (let [current-year (Integer/parseInt (last (clojure.string/split (str (java.util.Date.)) #" ")))
        parse-year (try
                     (Integer/parseInt fifth-input)
                     (catch Exception e
                       0))]
    (cond
      (or (not (= 4 (count fifth-input)))(= 0 parse-year)) (invalid-choice-menu sessionId (menu/age-menu-reset) 0 5) ;; invalid year return user to same menu
      (< (- current-year (Long/parseLong fifth-input)) 10)(invalid-choice-menu sessionId (menu/underage-menu-reset) 0 5)
      (> (- current-year (Long/parseLong fifth-input)) 85)(invalid-choice-menu sessionId (menu/overage-menu-reset) 0 5)
      :else (do
              (register-subscriber sessionId msisdn))))) ;; register user

(defn process-age-register
  "process age and register"
  [sessionId fifth-input msisdn]
  (case fifth-input
    nil (menu/age-menu)                ;; age menu
    (do
      (validate-year sessionId fifth-input msisdn)))) ;; check whether YOB is valid

(defn back-menu
  "navigate user to previous menu"
  [sessionId menu start end]
  (session/modify-menu sessionId start end)
  menu)

(defn deregister
  "Deregister user from service"
  [msisdn]
  (let [msisdn-exists (client/validate-msisdn config/username config/password msisdn)
        parse-msisdn (:status-msg (clojure.walk/keywordize-keys
                                    (json/read-str msisdn-exists)))]
    (case parse-msisdn
      0 (str config/unregistered-sub)     ;; subscriber not registered
      1 (str config/deregister-sub)       ;; subscriber successfully deregistered
      2 (str config/deregister-error))))  ;; error occured in operation

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
                                                (case fourth-input
                                                  ("1" "2" "3" "4" "5") (do
                                                                          (println "$$"))
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
                    "2" (do
                          (condp = second-input
                            "1" (do
                                  (condp = third-input
                                    "1" (do (process-age-register sessionId fifth-input msisdn))
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

(defn voice-handler
  "voice handler"
  [req]
  (log/infof "voice handler %s " req)
  (let [subscriber-number (:callerNumber (:params req))
        subscriber-number (Long/parseLong (subs subscriber-number 4))
        campaign-res (client/get-msisdn-campaigns subscriber-number)
        user-data (:user-data campaign-res)
        filter-campaign (atom [])]
    (log/infof "subscriber number %s %s" subscriber-number (type subscriber-number))
    (if (empty? user-data)
      (do
        (log/info "empty resultset for subscriber " subscriber-number)
        (util/unavailable-audio-voice-xml config/unavailable-audio-voice))
      (do
        (log/infof "campaign data for msisdn %s is %s" subscriber-number campaign-res)
        (doseq [a (:user-data campaign-res)]
          (if (= (count @filter-campaign) 2)
            (do
              (let [first-map (first @filter-campaign)
                    first-cost (:campaigncost (first @filter-campaign))
                    second-map (last @filter-campaign)
                    second-cost (:campaigncost (last @filter-campaign))
                    third-cost (:campaigncost a)
                    min-campaign (min first-cost second-cost third-cost)]
                (cond
                  (= min-campaign first-cost)
                  (do
                    (reset! filter-campaign [])
                    (swap! filter-campaign conj second-map a))
                  (= min-campaign second-cost)
                  (do
                    (reset! filter-campaign [])
                    (swap! filter-campaign conj first-map a))
                  (= min-campaign third-cost)
                  (do
                    (log/info "no changes")))))
            (do
              (swap! filter-campaign conj a))))
        (log/infof "Returned content for msisdn %s is %s and %s" subscriber-number @filter-campaign (:files (first @filter-campaign)))
        (if (= (count @filter-campaign) 1)
          (do
            (log/infof "one campaign retrieved %s %s" @filter-campaign (count @filter-campaign))
            (util/single-audio-voice-xml config/single-available-audio-voice (:files (first @filter-campaign))))
          (do
            (log/infof "two campaigns retrieved %s %s" @filter-campaign (count @filter-campaign))
            (util/double-audio-voice-xml config/double-available-audio-voice (:files (first @filter-campaign))(:files (second @filter-campaign))))))))
  #_(let [subscriber-number (-> req :params :callerNumber)]
      (log/infof "the subscriber number is % " subscriber-number)))

;; routes
(defroutes all-routes
           (POST "/mode/sikika/ussd/" [] ussd-handler)
           (POST "/mode/sikika/voice/" [] voice-handler)
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


