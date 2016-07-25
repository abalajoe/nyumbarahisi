(ns ussd.core-test
  (:use midje.sweet ussd.session)
  (:require [clojure.test :refer :all]
            [ussd.core :refer :all]
            [ussd.utils.util :as util]
            [clj-http.client :as http]
            [ussd.session :as sess]
            [ring.mock.request :as mock]
            [ussd.httpserver :as httpserver]
            [ussd.utils.config :as config]
            [clojure.data.json :as json]))

(config/load-configuration)
(sess/initialize-session)

(facts "USSD testing"
       (fact "Main Menu"
             (let [response (httpserver/app (mock/request
                                              :post "/mode/sikika/ussd/"
                                              {:sessionId "1234"
                                               :serviceCode "*223#"
                                               :phoneNumber 711651431
                                               :text ""}))]
               (:status response) => 200
               (:body response) => "CON Welcome to Sikika Registration\n\n Please enter your gender \n 1. Male \n 2. Female \n #. Quit \n"))
       (fact "Gender Menu"
             (let [response (httpserver/app (mock/request
                                              :post "/mode/sikika/ussd/"
                                              {:sessionId "1234"
                                               :serviceCode "*223#"
                                               :phoneNumber 711651431
                                               :text "1"}))]
               (:status response) => 200
               (:body response) => "CON Respond with: \n 1. Student \n 2. Employed \n 3. Unemployed \n 98. Back \n 00. Home \n 0. Quit \n"))
       (fact "Location Menu"
             (let [response (httpserver/app (mock/request
                                              :post "/mode/sikika/ussd/"
                                              {:sessionId "1234"
                                               :serviceCode "*223#"
                                               :phoneNumber 711651431
                                               :text "1*1"}))]
               (:status response) => 200
               (:body response) => "CON Respond with: \n 1. Nairobi \n 2. Coast \n 3. North Eastern \n 4. Eastern \n 5. Central\n 6. Rift Valley \n 7. Western \n 8. Nyanza \n 98. Back \n 00. Home \n 0. Quit \n"))
       (fact "County Menu"
             (let [response (httpserver/app (mock/request
                                              :post "/mode/sikika/ussd/"
                                              {:sessionId "1234"
                                               :serviceCode "*223#"
                                               :phoneNumber 711651431
                                               :text "1*1*1"}))]
               (:status response) => 200
               (:body response) => "CON Respond with: \n 1. Nairobi \n 98. Back \n 00. Home \n 0. Quit \n"))
       (fact "Age Menu"
             (let [response (httpserver/app (mock/request
                                              :post "/mode/sikika/ussd/"
                                              {:sessionId "1234"
                                               :serviceCode "*223#"
                                               :phoneNumber 711651431
                                               :text "1*1*1*1"}))]
               (:status response) => 200
               (:body response) => "CON Please enter your Date of Birth:\n\n"))

       (fact "Registration"
             (let [response (httpserver/app (mock/request
                                              :post "/mode/sikika/ussd/"
                                              {:sessionId "1234"
                                               :serviceCode "*223#"
                                               :phoneNumber 711651431
                                               :text "1*1*1*1*1990"}))]
               (:status response) => 200
               (:body response) => "END You are successfully registered to Sikika Service. Please dial 887 to listen to your first message and get free airtime.")))

