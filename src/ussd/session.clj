(ns ussd.session
  ^{:author "jabala"
    :doc "Session Management"
    :added "1.0"
    }
  (:require [clojure.tools.logging :as log]))

(defn initialize-session []
  (log/info "Initializing session-manager..")
  (def session-state (atom {})))

;(initialize-session)

(defn get-session
  "get session"
  []
  @session-state)

(defn start-session
  "Start subscriber session.
   Check if the session ID of a request is already in the atom 'session-state'.
   If the session ID is not in the atom, add it. If it is in the atom, do nothing.
   The session ID is added as the keyword in the atom, with the user input journey
   as the value, in a vector i.e:

                {:1234 [1 2 3], :53342 [1 1 4], :2144 []}

   In the above code you can see that the session ID 1234 is the keyword and its value
   is a vector containing what the user inputs throughout the registration process. Also
   note that this function first checks if a session ID is present in the map, if not it
   is appended as a key with an empty vector at first.
    "
  [session-id]
  (log/debug "Starting session.." @session-state)
  (when-not (contains? @session-state (keyword (str session-id))) ;; check if session ID is in map
    (swap! session-state assoc (keyword (str session-id)) [])))   ;; add session ID if not in map

;(start-session 1234)

(defn get-session-data
  "Get session data.
   Access all user input in the registration process.
   Use the session ID, which is a keyword, to access the vectorized user input."
  [session-id]
  (let [key-word (keyword (str session-id))                 ;; change the session id to keyword
        session (key-word @session-state)]                  ;; access the vector of user inputs
    session))                                               ;; return vector

;(get-session-data 1234)

(defn update-session
  "Update session.
  Get the current user input for given session and add the new user input to vector."
  [session-id text]
  (let [session (get-session-data session-id)               ;; get the current user inputs
        update-vector (conj session text)                   ;; append new user input
        update-session (swap! session-state assoc (keyword (str session-id)) update-vector)] ;; update session data vector for this session ID
    update-session))

(defn modify-menu
  "Navigate subscriber to previous menu"
  [session-id start end]
  (let [session (get-session-data session-id)               ;; get the current user inputs
        _ (println "session > " session)
        update-vector (subvec session start end)            ;; chop off the last input
        _ (println "update-vector > " update-vector)
        update-session (swap! session-state assoc (keyword (str session-id)) update-vector)
        _ (println "update-session > " update-session)] ;; update vector
    update-session))  ;; return updated vector

;(update-session 1234 12)

(defn reset-session
  "Reset session"
  [session-id]
  (swap! session-state assoc (keyword (str session-id)) []))

(defn clear-session
  "clear session & data"
  [session-id]
  (log/infof "clearing session data for session id [%s]" session-id)
  (swap! session-state dissoc (keyword (str session-id))))