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
                     :data saved-user}})))))

(defn set-user! [{:keys [query-fn]} request]
  (let [session (:session request)
        username (get-in request [:parameters :body :username])
        password (get-in request [:parameters :body :password])
        id (query-fn :login-guest {:username username :password password})]
    (log/debug "Create Login: " session username password id)
    (when id
      (-> (http-response/ok {:time     (str (Date. (System/currentTimeMillis)))
                             :up-since (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
                             :app      {:message "User logged in"
                                        :data id}})
          (assoc :session (assoc session :user id))))))

(defn unset-user! [_ request]
  (let [session (:session request)
        user-id (get-in request [:parameters :body :user-id])]
    (log/debug "Logout user " session user-id)
    (-> (http-response/ok {:time     (str (Date. (System/currentTimeMillis)))
                           :up-since (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
                           :app      {:message "User logged out"
                                      :data 0}})
        (assoc :session (dissoc session :user)))))

(defn logged-in? [_ request]
  (let [session (:session request)]
    (log/debug "Session" session)
    (http-response/ok {:time     (str (Date. (System/currentTimeMillis)))
                       :up-since (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
                       :app      {:message "Session"
                                  :data session}})))



