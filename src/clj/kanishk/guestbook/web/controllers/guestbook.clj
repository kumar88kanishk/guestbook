(ns kanishk.guestbook.web.controllers.guestbook
  (:require
   [ring.util.http-response :as http-response]
   [clojure.tools.logging :as log])
  (:import
   [java.util Date]))

(defn create-message!
  [{:keys [query-fn]} request]
  (log/debug "Saving message" (get-in request [:parameters :body :name]) (get-in request [:parameters :body :message]))
  (let [name (get-in request [:parameters :body :name])
        message (get-in request [:parameters :body :message])]
    (if (or (empty? name) (empty? message))
      (http-response/method-not-allowed
       {:time     (str (Date. (System/currentTimeMillis)))
        :up-since (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
        :app      {:message  (cond (and (empty? name) (empty? message)) "Name and Message are required"
                                   (empty? name) "Name is required"
                                   (empty? message) "Message is required")}})
      (let [saved-message (query-fn :save-message! {:name name :message message})]
        (http-response/ok
         {:time     (str (Date. (System/currentTimeMillis)))
          :up-since (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
          :app      {:message "Message Saved"
                     :created-message saved-message}})))))


(defn get-messages
  [{:keys [query-fn]} request]
  (log/debug "Saving message" (get-in request [:parameters :body :name]) (get-in request [:parameters :body :message]))
  (let [messages (query-fn :list-messages {})]
    (http-response/ok
     {:time     (str (Date. (System/currentTimeMillis)))
      :up-since (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
      :app      {:message "Messages"
                 :data messages}}))

  )

(defn get-message
  [{:keys [query-fn]} request]
  (log/debug "Get mEssage by id"  (get-in request [:parameters :query :id]))
  (let [message (query-fn :get-message-by-id {:id (get-in request [:parameters :query :id])})]
    (http-response/ok
     {:time (str (Date. (System/currentTimeMillis)))
      :upsince (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
      :app {:message message}})))


(defn update-message!
  [{:keys [query-fn]} request]
  (log/debug "Update message by id" (get-in request [:parameters :query]) (get-in request [:parameters :body]))
  (let [id (get-in request [:parameters :query :id])
        name (get-in request [:parameters :body :name])
        message (get-in request [:parameters :body :message])
        updated-message (query-fn :update-message {:id id :name name :message message})]
    (http-response/ok
     {:time (str (Date. (System/currentTimeMillis)))
      :upsince (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
      :app {:message updated-message}})))


