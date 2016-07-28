(ns ussd.utils.config
  ^{:author "joeabala"
    :doc "Load configurations"
    :added "1.0"
    }
  (:require [propertea.core :as propertea]
            [clojure.tools.logging :as log]))


;; configuration-file path
(def config "/Users/abala/abala/projects/albo/nyumbarahisi/nyumbarahisi/src/config.properties")

; get configuration from properties file
(defn get-configuration
  "Function gets reads from configuration
   file and gets the values"
  [key]
  ((propertea/read-properties config)key))


;(get-config)

(defn load-configuration
  "Function loads configuration file"
  []
  (log/infof "Loading configuration")
  (def server-port (Integer/parseInt (get-configuration :port)))


  ;; menu
  (def main-menu  (get-configuration :main-menu))
  (def reset-main-menu  (get-configuration :reset-main-menu))
  (def plot-menu  (get-configuration :plot-menu))
  (def exit-menu  (get-configuration :exit-menu))
  (def location-menu  (get-configuration :location-menu))
  (def cost-menu  (get-configuration :cost-menu))
  (def msisdn-menu  (get-configuration :msisdn-menu))
  (def reset-cost-menu  (get-configuration :cost-menu-reset))
  (def reset-msisdn-menu  (get-configuration :msisdn-menu-reset))
  (def region-menu  (get-configuration :region-menu))
  (def region-menu-reset  (get-configuration :region-menu-reset))
  (def successful-registration-menu  (get-configuration :successful-registration-menu))
  (def unsuccessful-registration-menu  (get-configuration :unsuccessful-registration-menu))
  (def nairobi-county-menu  (get-configuration :nairobi-county-menu))
  (def coast-county-menu  (get-configuration :coast-county-menu))
  (def north-eastern-county-menu  (get-configuration :north-eastern-county-menu))
  (def eastern-county-menu  (get-configuration :eastern-county-menu))
  (def central-county-menu  (get-configuration :central-county-menu))
  (def riftvalley-county-menu  (get-configuration :riftvalley-county-menu))
  (def western-county-menu  (get-configuration :western-county-menu))
  (def nyanza-county-menu  (get-configuration :nyanza-county-menu))
  (def reset-menu  (get-configuration :reset-menu))
  (def reset-gender-menu  (get-configuration :reset-gender-menu))
  (def reset-plot-menu  (get-configuration :reset-plot-menu))
  (def reset-occupation-menu  (get-configuration :reset-occupation-menu))
  (def reset-location-menu  (get-configuration :reset-location-menu))
  (def reset-register-menu  (get-configuration :reset-register-menu))
  (def reset-nairobi-county-menu  (get-configuration :reset-nairobi-county-menu))
  (def reset-coast-county-menu  (get-configuration :reset-coast-county-menu))
  (def reset-north-eastern-county-menu  (get-configuration :reset-north-eastern-county-menu))
  (def reset-central-county-menu  (get-configuration :reset-central-county-menu))
  (def reset-eastern-county-menu  (get-configuration :reset-eastern-county-menu))
  (def reset-riftvalley-county-menu  (get-configuration :reset-riftvalley-county-menu))
  (def reset-western-county-menu  (get-configuration :reset-western-county-menu))
  (def reset-nyanza-county-menu  (get-configuration :reset-nyanza-county-menu))
  (def register-menu  (get-configuration :register-menu))
(log/info "finished loading conf"))
