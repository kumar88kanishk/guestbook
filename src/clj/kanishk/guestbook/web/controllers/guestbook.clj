(ns kanishk.guestbook.web.controllers.guestbook
  (:require 
   [ring.util.http-response :as http-response]
   [clojure.tools.logging :as log])
  (:import
   [java.util Date]))

(defn save-message! 
  [{:keys [query-fn]} {{:strs [name message]} :form-params :as request}]
  (log/debug "Saving message" name message)
  (try 
    (if (or (empty? name) (empty? message))
      (cond-> (http-response/found "/")
        (empty? name)
        (assoc-in [:flash :errors :name] "Name is required")
        (empty? message)
        (assoc-in [:flash :erros :name] "Message is required"))
      (do
        (query-fn :save-message! {:name name :message message})
        (http-response/found "/")))
    (catch Exception e
      (log/error e "Could not save")
      (-> (http-response/found "/")
          (assoc :flash {:errors {:unknown (.getMessage e)}})))))