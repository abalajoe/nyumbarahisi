(ns ussd.utils.util
  ^{:author "jabala"
    :doc "Helper Functions"
    :added "1.0"
    }
  (:require [clojure.tools.logging :as log]
            [ussd.session :as session]
            [clojure.string :as s]
            [ussd.model.datasource :as db]
            [ussd.utils.menu :as menu]))

(defn parse-data
  "Function parses final ussd string"
  [ussd-str]
  (let [location (second ussd-str)
        county (get ussd-str 2)
        plot (get ussd-str 3)
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
        parse-plot (case plot
                     "1" "Bedsitter"
                     "2" "One Bedroom"
                     "3" "Two Bedroom"
                     "4" "Three Bedroom"
                     "5" "Four Bedroom"
                     )]
    (log/info "Details [" (s/lower-case parse-location) (s/lower-case parse-county) (s/lower-case parse-plot)"]")
    (db/get-houses (s/lower-case parse-location) (s/lower-case parse-county) (s/lower-case parse-plot))))

(defn parse-res
  "Function parses final ussd string"
  [sessionId ussd-str]
  (let [user (first ussd-str)
        location (second ussd-str)
        county (get ussd-str 2)
        region (get ussd-str 3)
        plot (get ussd-str 4)
        cost (get ussd-str 5)
        msisdn (get ussd-str 6)
        parse-user (case user
                       "1" "owner"
                       "2" "client")
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
        parse-plot (case plot
                     "1" "Bedsitter"
                     "2" "One Bedroom"
                     "3" "Two Bedroom"
                     "4" "Three Bedroom"
                     "5" "Four Bedroom"
                     )
        ]
    (session/clear-session sessionId)

    (let [location (s/lower-case parse-location)
          county (s/lower-case parse-county)
          region (s/replace (s/lower-case region) #" " "")
          plot (s/lower-case parse-plot)]
      (log/info "Details [" parse-user location county region plot cost msisdn"]")
      (if (not= 0 (db/reg-owner location county region plot (Long/parseLong cost) (Long/parseLong msisdn)))
        (do
          (log/info "successfully registered owner")
          (menu/successful-registration-menu))
        (do
          (log/info "problem registering owner")
          (menu/unsuccessful-registration-menu))))))