(ns kanishk.guestbook.web.controllers.guests
  (:require
   [ring.util.http-response :as http-response]
   [clojure.tools.logging :as log])
  (:import
   [java.util Date]))

(defn create-guest!
  [{:keys [query-fn]} request]
  (log/debug "Creating guest" (get-in request [:parameters :body :username]) (get-in request [:parameters :body :password]))
  (let [username (get-in request [:parameters :body :username])
        password (get-in request [:parameters :body :password])]
    (if (or (empty? username) (empty? password))
      (http-response/method-not-allowed
       {:time     (str (Date. (System/currentTimeMillis)))
        :up-since (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
        :app      {:password  (cond (and (empty? username) (empty? password)) "username and password are required"
                                   (empty? username) "username is required"
                                   (empty? password) "password is required")}})
      (let [saved-user (query-fn :create-guest! {:username username :password password})]
        (http-response/ok
         {:time     (str (Date. (System/currentTimeMillis)))
          :up-since (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
          :app      {:message "User created"
                     :created-message saved-user}})))))