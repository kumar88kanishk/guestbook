(ns kanishk.guestbook.web.routes.utils
  (:import
   [java.util Date]))

(def route-data-path [:reitit.core/match :data])

(defn route-data
  [req]
  (get-in req route-data-path))

(defn route-data-key
  [req k]
  (get-in req (conj route-data-path k)))

(defn default-response-params [message data]
  {:time     (str (Date. (System/currentTimeMillis)))
   :up-since (str (Date. (.getStartTime (java.lang.management.ManagementFactory/getRuntimeMXBean))))
   :app {:message message :data data}})
