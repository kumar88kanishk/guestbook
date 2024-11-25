(ns kanishk.guestbook.web.controllers.guestbook
  (:require
   [ring.util.http-response :as http-response]
   [clojure.tools.logging :as log])
  (:import
   [java.util Date]))

(defn save-message!
  [{:keys [query-fn body-params]} {{:strs [_ _]} :form-params :as request}]
  (log/debug "Saving message" (get-in request [:parameters :body :name]) (get-in request [:parameters :body :message]))
  (try
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
                       :created-message saved-message}}))))
    (catch Exception e
      (log/error e "Could not save")
      (-> (http-response/ok
           {:time     (str (Date. (System/currentTimeMillis)))
            :up-since (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
            :app      {:message "Message could not be saved"}})
          (assoc :flash {:errors {:unknown (.getMessage e)}})))))


(defn list-messages
  [{:keys [query-fn body-params]} {{:strs [_ _]} :form-params :as request}]
  (log/debug "Saving message" (get-in request [:parameters :body :name]) (get-in request [:parameters :body :message]))
  (try
    (let [messages (query-fn :list-messages {})]
      (http-response/ok
       {:time     (str (Date. (System/currentTimeMillis)))
        :up-since (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
        :app      {:message "Messages"
                   :data messages}}))

    (catch Exception e
      (log/error e "Could not list")
      (-> (http-response/ok
           {:time     (str (Date. (System/currentTimeMillis)))
            :up-since (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
            :app      {:message "Messages could not be listed"}})))))

(defn get-message
  [{:keys [query-fn]} request]
  (log/debug "Get mEssage by id" request (get-in request [:parameters :query :id]))
  (let [message (query-fn :get-message-by-id {:id (get-in request [:parameters :query :id])})]
    (http-response/ok
     {:time (str (Date. (System/currentTimeMillis)))
      :upsince (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
      :app {:message message}})))


