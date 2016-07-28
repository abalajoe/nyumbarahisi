(ns ussd.model.datasource
  ^{:author "joeabala"
    :doc    "Database configurations"
    :added  "1.0"
    }
  (:require [ussd.utils.config :as config]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log])
  (:use korma.core)
  (:use korma.db)
  (:import (java.sql SQLException)))

;==================================================================================================
;;                                  DATABASE SPECIFICATIONS
;==================================================================================================


(defdb db (postgres {:db       "nyumbarahisi"
                     :user       "postgres"
                     :password   "root"
                     :host       "localhost"
                     :port       "5432"
                     :delimiters ""}))


;==================================================================================================
;;                                  DATABASE FUNCTIONS
;==================================================================================================



(defn get-houses
  "get house"
  [location county region plot]
  (println ":" location county region plot)
  (try
    (exec-raw db ["select * from tbl_owners where location = ? and county = ? and region = ? and plot = ?"
                  [location county region plot]] :results)
    (catch SQLException sql
      (log/error "get-houses SQL => " (.getMessage sql))
      0)
    (catch Exception e
      (log/error "get-houses E => " (.getMessage e))
      0)))


(defn reg-owner
  "Function registers owner"
  [location county region plot cost mobile]
  (println "== " location county region plot cost mobile " ==")
  (try
    (exec-raw db ["insert into tbl_owners (location, county, region, plot, cost, mobile)
                   values (?,?,?,?,?,?)" [location county region plot cost mobile]])
    (catch SQLException sql
      (log/error "reg-owner SQL => " (.getMessage sql))
      0)
    (catch Exception e
      (log/error "reg-owner E => " (.getMessage e))
      0)))
