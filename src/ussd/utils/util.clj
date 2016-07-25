(ns ussd.utils.util
  ^{:author "jabala"
    :doc "Helper Functions"
    :added "1.0"
    }
  (:require [clojure.tools.logging :as log]
            [ussd.httpclient :as client]
            [ussd.session :as session]
            [ussd.utils.config :as config]
            [clojure.data.json :as json]))

(defn single-audio-voice-xml
  "voice xml response for single audio"
  [message audio-file]
  (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response><Say>" message "</Say><Play url=\"http://197.248.0.204:3014/audio/" audio-file \" "/></Response>"))

(defn unavailable-audio-voice-xml
  "voice xml response for unavailable audio"
  [message]
  (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response><Say>" message "</Say></Response>"))

(defn double-audio-voice-xml
  "voice xml response for two audion"
  [message audio-file1 audio-file2]
  (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response><Say>" message "</Say><Play url=\"http://197.248.0.204:3014/audio/" audio-file1 \" "/><Play url=\"http://197.248.0.204:3014/audio/" audio-file2 " \"/></Response>"))


#_(defn voice-xml
  "voice xml response"
  [message audio-file]
  (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response><Say>" message "</Say><Play url=\"http://www.myvoicemailserver.com/audio/" audio-file \"" /></Response>"))

;(unavailable-audion-voice-xml "hello thank you")

(defn parse-res
  "Function parses final ussd string"
  [sessionId ussd-str msisdn]
  (let [gender (first ussd-str)
        occupation (second ussd-str)
        location (get ussd-str 2)
        county (get ussd-str 3)
        year-birth (get ussd-str 4)
        current-year (Integer/parseInt (last (clojure.string/split (str (java.util.Date.)) #" ")))
        age (- current-year (Integer/parseInt year-birth))
        parse-gender (case gender
                       "1" "male"
                       "2" "female")
        parse-occupation (case occupation
                       "1" "student"
                       "2" "employed"
                       "3" "unemployed"
                       )
        parse-location (case location
                         "1" "Nairobi"
                         "2" "Coast"
                         "3" "North Eastern"
                         "4" "Eastern"
                         "5" "Central"
                         "6" "Rift Valley"
                         "7" "Western"
                         "8" "Nyanza"
                         )
        parse-county (condp = parse-location
                       "Nairobi" (case county
                                   "1" "Nairobi")
                       "Coast" (case county
                                 "1" "Mombasa"
                                 "2" "Kwale"
                                 "3" "Kilifi"
                                 "4" "Tana River"
                                 "5" "Lamu"
                                 "6" "Taita Taveta"
                                 )
                       "North Eastern" (case county
                                         "1" "Garissa"
                                         "2" "Wajir"
                                         "3" "Mandera"
                                         )
                       "Eastern" (case county
                                   "1" "Marsabit"
                                   "2" "Isiolo"
                                   "3" "Meru"
                                   "4" "Tharaka-Nithi"
                                   "5" "Embu"
                                   "6" "Kitui"
                                   "7" "Machakos"
                                   "8" "Makueni"
                                   )
                       "Central" (case county
                                   "1" "Nyandarua"
                                   "2" "Nyeri"
                                   "3" "Kirinyaga"
                                   "4" "Muranga"
                                   "5" "Kiambu"
                                   )
                       "Rift Valley" (case county
                                       "1" "Turkana"
                                       "2" "West Pokot"
                                       "3" "Samburu"
                                       "4" "Trans-Nzoia"
                                       "5" "Uasin Ngishu"
                                       "6" "Elgeyo Marakwet"
                                       "7" "Nandi"
                                       "8" "Baringo"
                                       "9" "Laikipia"
                                       "10" "Nakuru"
                                       "11" "Narok"
                                       "12" "Kajiado"
                                       "13" "Kericho"
                                       "14" "Bomet"
                                       )
                       "Western" (case county
                                   "1" "Kakamega"
                                   "2" "Vihiga"
                                   "3" "Bungoma"
                                   "4" "Busia")
                       "Nyanza" (case county
                                  "1" "Siaya"
                                  "2" "Kisumu"
                                  "3" "Homa Bay"
                                  "4" "Migori"
                                  "5" "Kisii"
                                  "6" "Nyamira"
                                  ))
        ]
    (session/clear-session sessionId)
    (log/info parse-gender parse-occupation parse-location parse-county year-birth msisdn age)
    ;; send subscriber details to elasticsearch
    (log/info "storing client data => " config/username config/password
              msisdn parse-gender age parse-location parse-county parse-occupation)
    (client/elasticsearch-ussd config/username config/password
                               msisdn parse-gender age parse-location parse-county parse-occupation)
    (log/info "submiting data to campaign management => "
              msisdn parse-gender age parse-location parse-county parse-occupation)

    (client/generate-target-list msisdn parse-gender age parse-location parse-county parse-occupation)
    (log/info "==== registering client ===")
    ;; send subscriber detais to subscriber registration service
    (let [reg-subscriber (client/register-subscriber config/username config/password msisdn config/channel
                                                     (json/json-str (assoc {} :gender (clojure.string/lower-case parse-gender)
                                                                              :age (clojure.string/lower-case age)
                                                                              :location (clojure.string/lower-case parse-location)
                                                                              :county (clojure.string/lower-case parse-county)
                                                                              :occupation (clojure.string/lower-case parse-occupation))))]
      (str "END " (:status-msg (clojure.walk/keywordize-keys (json/read-str reg-subscriber)))))))