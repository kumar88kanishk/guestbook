(ns kanishk.guestbook.web.controllers.guestbook
  (:require
   [ring.util.http-response :as http-response]
   [kanishk.guestbook.web.routes.utils :as utils]
   [clojure.tools.logging :as log]))

(defn create-message!
  [{:keys [query-fn]} request]
  (log/debug "Saving message" (get-in request [:parameters :body :name]) (get-in request [:parameters :body :message]))
  (let [name (get-in request [:parameters :body :name])
        message (get-in request [:parameters :body :message])]
    (if (or (empty? name) (empty? message))
      (http-response/method-not-allowed
       (utils/default-response-params (cond (and (empty? name) (empty? message)) "Name and Message are required"
                                      (empty? name) "Name is required"
                                      (empty? message) "Message is required") {}))
      (let [saved-message (query-fn :save-message! {:name name :message message})]
        (http-response/ok
         (utils/default-response-params "Message Saved" saved-message))))))


(defn get-messages
  [{:keys [query-fn]} request]
  (log/debug "Saving message" (get-in request [:parameters :body :name]) (get-in request [:parameters :body :message]))
  (let [messages (query-fn :list-messages {})]
    (http-response/ok
     (utils/default-response-params "Messages" messages))))

(defn get-message
  [{:keys [query-fn]} request]
  (log/debug "Get mEssage by id"  (get-in request [:parameters :query :id]))
  (let [message (query-fn :get-message-by-id {:id (get-in request [:parameters :query :id])})]
    (http-response/ok
     (utils/default-response-params "Message Found" message))))


(defn update-message!
  [{:keys [query-fn]} request]
  (log/debug "Update message by id" (get-in request [:parameters :query]) (get-in request [:parameters :body]))
  (let [id (get-in request [:parameters :query :id])
        name (get-in request [:parameters :body :name])
        message (get-in request [:parameters :body :message])
        updated-message (query-fn :update-message {:id id :name name :message message})]
    (http-response/ok
     (utils/default-response-params "Update Message" updated-message))))


