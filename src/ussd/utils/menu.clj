(ns ussd.utils.menu
  ^{:author "jabala"
    :doc "Menu"
    :added "1.0"
    }
  (:require [ussd.utils.config :as config]))

(defn main-menu
  "Display main menu"
  []
  (str config/main-menu))

(defn reset-main-menu
  "Display main menu"
  []
  (str config/reset-main-menu))

(defn voice-response
  "Display main menu"
  []
  (str config/voice-response))

(defn gender-menu
  "Display gender menu"
  []
  (str config/gender-menu))

(defn plot-menu
  "Display relationship status menu"
  []
  (str config/plot-menu))

(defn reset-plot-menu
  "Display relationship status menu"
  []
  (str config/reset-plot-menu))

(defn occupation-menu
  "Display occupation status menu"
  []
  (str config/occupation-menu))

(defn exit-menu
  "Exit session message"
  []
  (str config/exit-menu))

(defn location-menu
  "Display location menu"
  []
  (str config/location-menu))

(defn age-menu
  "Display age menu"
  []
  (str config/age-menu))

(defn age-menu-reset
  "Display age menu when invalid age is entered"
  []
  (str config/age-menu-reset))

(defn underage-menu-reset
  "Display age menu when invalid age is entered"
  []
  (str config/underage-menu-reset))

(defn overage-menu-reset
  "Display age menu when invalid age is entered"
  []
  (str config/overage-menu-reset))

(defn nairobi-county-menu
  "Displays Nairobi county menu"
  []
  (str config/nairobi-county-menu))

(defn coast-county-menu
  "Display Coast county menu"
  []
  (str config/coast-county-menu))

(defn north-eastern-county-menu
  "Display North Eastern county menu"
  []
  (str config/north-eastern-county-menu))

(defn eastern-county-menu
  "Display Eastern county menu"
  []
  (str config/eastern-county-menu))

(defn central-county-menu
  "Display Central county menu"
  []
  (str config/central-county-menu))

(defn riftvalley-county-menu
  "Display Rift Valley county menu"
  []
  (str config/riftvalley-county-menu))

(defn western-county-menu
  "Display Eastern county menu"
  []
  (str config/western-county-menu))

(defn nyanza-county-menu
  "Display Eastern county menu"
  []
  (str config/nyanza-county-menu))

(defn reset-menu
  "Display reset menu"
  []
  (str config/reset-menu))

(defn reset-gender-menu
  "Display reset gender menu"
  []
  (str config/reset-gender-menu))


(defn reset-occupation-menu
  "Display reset occupation status menu"
  []
  (str config/reset-occupation-menu))

(defn reset-location-menu
  "Display reset location menu"
  []
  (str config/reset-location-menu))

(defn reset-cost-menu
  "Display reset cost menu"
  []
  (str config/reset-cost-menu))

(defn cost-menu
  "cost menu"
  []
  (str config/cost-menu))

(defn reset-msisdn-menu
  "Display reset msisdn menu"
  []
  (str config/reset-msisdn-menu))

(defn msisdn-menu
  "msisdn menu"
  []
  (str config/msisdn-menu))

(defn successful-registration-menu
  "successful-registration-menu"
  []
  (str config/successful-registration-menu))

(defn unsuccessful-registration-menu
  "unsuccessful-registration-menu"
  []
  (str config/unsuccessful-registration-menu))

(defn reset-register-menu
  "Display reset register menu"
  []
  (str config/reset-register-menu))

(defn reset-nairobi-county-menu
  "Display reset Nairobi county menu"
  []
  (str config/reset-nairobi-county-menu))

(defn reset-coast-county-menu
  "Display reset Coast county menu"
  []
  (str config/reset-coast-county-menu))

(defn reset-north-eastern-county-menu
  "Display reset North Eastern county menu"
  []
  (str config/reset-north-eastern-county-menu))

(defn reset-central-county-menu
  "Display reset Central county menu"
  []
  (str config/reset-central-county-menu))

(defn reset-eastern-county-menu
  "Display reset Eastern county menu"
  []
  (str config/reset-eastern-county-menu))

(defn reset-riftvalley-county-menu
  "Display reset Rift Valley county menu"
  []
  (str config/reset-riftvalley-county-menu))

(defn reset-western-county-menu
  "Display reset Western county menu"
  []
  (str config/reset-western-county-menu))

(defn reset-nyanza-county-menu
  "Display reset Nyanza county menu"
  []
  (str config/reset-nyanza-county-menu))

(defn register-menu
  "Function displays register menu"
  []
  (str config/register-menu))
