(ns ussd.utils.config
  ^{:author "jabala"
    :doc "Load configurations"
    :added "1.0"
    }
  (:require [propertea.core :as propertea]
            [clojure.tools.logging :as log]))


;; configuration-file path
;(def config "/opt/sikika/ussd/config.properties")
;(def config "E:\\sikika\\ussdconfig.properties")
(def config "E:\\sikika\\albo\\src\\config.properties")

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
  (def register-subscriber-url  (get-configuration :register-subscriber-url))
  (def validate-msisdn-url  (get-configuration :validate-msisdn-url))
  (def elasticsearch-url  (get-configuration :elasticsearch-url))
  (def check-msisdn-url (get-configuration :check-msisdn-url))
  (def url  (get-configuration :url))
  (def username  (get-configuration :username))
  (def password  (get-configuration :password))
  (def channel  (get-configuration :channel))
  (def elasticsearch-url (get-configuration :elasticsearch-url))
  (def target-list-url (get-configuration :target-list-url))
  (def index  (get-configuration :index))
  (def mapping-type  (get-configuration :mapping-type))

  ;; sikika voice call
  (def sikika-username (get-configuration :sikika-username))
  (def sikika-apikey (get-configuration :sikika-apikey))
  (def sikika-virtual-number (get-configuration :sikika-virtual-number))
  (def unavailable-audio-voice (get-configuration :unavailable-audio-voice))
  (def single-available-audio-voice (get-configuration :single-available-audio-voice))
  (def double-available-audio-voice (get-configuration :double-available-audio-voice))

  ;; deregistration
  (def unregistered-sub  (get-configuration :unregistered-sub))
  (def deregister-sub  (get-configuration :deregister-sub))
  (def deregister-error  (get-configuration :deregister-error))

  ;; menu
  (def main-menu  (get-configuration :main-menu))
  (def reset-main-menu  (get-configuration :reset-main-menu))
  (def voice-response  (get-configuration :voice-response))
  (def gender-menu  (get-configuration :gender-menu))
  (def plot-menu  (get-configuration :plot-menu))
  (def occupation-menu  (get-configuration :occupation-menu))
  (def exit-menu  (get-configuration :exit-menu))
  (def location-menu  (get-configuration :location-menu))
  (def cost-menu  (get-configuration :cost-menu))
  (def msisdn-menu  (get-configuration :msisdn-menu))
  (def reset-cost-menu  (get-configuration :cost-menu-reset))
  (def reset-msisdn-menu  (get-configuration :msisdn-menu-reset))
  (def age-menu  (get-configuration :age-menu))
  (def region-menu  (get-configuration :region-menu))
  (def age-menu-reset  (get-configuration :age-menu-reset))
  (def region-menu-reset  (get-configuration :region-menu-reset))
  (def underage-menu-reset  (get-configuration :underage-menu-reset))
  (def overage-menu-reset  (get-configuration :overage-menu-reset))
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
(log/info "finished loading conf")
  #_(log/infof "finished loading conf:  [%s %s %s %s %s %s %s %s %s %s %s]"
             server-port register-subscriber-url validate-msisdn-url
             username password channel elasticsearch-url unregistered-sub
             deregister-sub deregister-error main-menu)
  #_(log/debugf "finished loading conf:  [%s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s]"
              main-menu gender-menu status-menu exit-menu location-menu age-menu age-menu-reset nairobi-county-menu coast-county-menu
              north-eastern-county-menu eastern-county-menu central-county-menu riftvalley-county-menu western-county-menu nyanza-county-menu
              reset-menu reset-gender-menu reset-status-menu reset-location-menu reset-register-menu reset-nairobi-county-menu reset-coast-county-menu
              reset-north-eastern-county-menu reset-central-county-menu reset-eastern-county-menu reset-riftvalley-county-menu reset-western-county-menu
              reset-nyanza-county-menu register-menu))
