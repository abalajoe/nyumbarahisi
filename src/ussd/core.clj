(ns ussd.core (:gen-class)
  ^{:author "jabala"
    :doc "The Sikika USSD Service API"
    :added "1.0"
    }
  (:require [clojure.tools.logging :as log]
            [ussd.httpserver :as http]
            [ussd.session :as session]
            [ussd.utils.config :as config]))

;; called when the program exits
(defn end-program
  "Function shuts application down gracefully"
  []
  ; stop server
  (http/stop-server ))

(defn -main
  "main entry of application"
  [& args]
  (log/info "===================================================")
  (log/info "SIKIKA USSD ADAPTER API - VERSION 1.1.0")
  (log/info "===================================================")

  (config/load-configuration)                               ;; load configuration
  (http/initialize-http )                                   ;; initialize http server
  (session/initialize-session)                              ;; initialize session

  ;; register runtime hook
  (.addShutdownHook (Runtime/getRuntime) (Thread. end-program))

  ;; start server
  (try
    (http/start-server)
    (catch Exception e
      (do
        (log/error "start server E: " (.getMessage e))
        ;; end program gracefully
        (end-program)))))

(-main)

