(ns kanishk.guestbook.web.controllers.guests
  (:require
   [ring.util.http-response :as http-response]
   [kanishk.guestbook.web.routes.utils :as utils]
   [clojure.tools.logging :as log]))

(defn create-guest!
  [{:keys [query-fn]} request]
  (log/debug "Creating guest" (get-in request [:parameters :body :username]) (get-in request [:parameters :body :password]))
  (let [username (get-in request [:parameters :body :username])
        password (get-in request [:parameters :body :password])]
    (if (or (empty? username) (empty? password))
      (http-response/method-not-allowed (utils/default-response-params (cond (and (empty? username) (empty? password)) "username and password are required"
                                                                             (empty? username) "username is required"
                                                                             (empty? password) "password is required") ""))
      (let [saved-user (query-fn :create-guest! {:username username :password password})]
        (http-response/ok (utils/default-response-params "User created" saved-user))))))

(defn set-user! [{:keys [query-fn]} request]
  (let [session (:session request)
        username (get-in request [:parameters :body :username])
        password (get-in request [:parameters :body :password])
        id (query-fn :login-guest {:username username :password password})]
    (log/debug "Create Login: " session username password id)
    (if id
      (-> (http-response/ok (utils/default-response-params "User logged in" id))
          (assoc :session (assoc session :user id)))
      (http-response/not-found (utils/default-response-params "User not found" 0)))))

(defn unset-user! [{:keys [query-fn]} request]
  (let [session (:session request)
        user-id (get-in request [:parameters :body :user-id])
        valid-user? (query-fn :valid-user? {:user-id user-id})]
    (log/debug "Logout user " session user-id valid-user?)
    (if valid-user?
      (-> (http-response/ok (utils/default-response-params "User logged out" 0))
          (assoc :session (dissoc session :user)))
      (http-response/not-found (utils/default-response-params "User not found" 0)))))

(defn logged-in? [_ request]
  (let [session (:session request)]
    (log/debug "Session" session)
    (http-response/ok (utils/default-response-params "Session" session))))



