(ns kanishk.guestbook.web.routes.api
  (:require
   [kanishk.guestbook.web.controllers.health :as health]
   [kanishk.guestbook.web.controllers.guestbook :as guestbook]
   [kanishk.guestbook.web.controllers.guests :as guests]
   [kanishk.guestbook.web.middleware.exception :as exception]
   [kanishk.guestbook.web.middleware.formats :as formats]
   [integrant.core :as ig]
   [reitit.coercion.malli :as malli]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.swagger :as swagger]))

(def route-data
  {:coercion   malli/coercion
   :muuntaja   formats/instance
   :swagger    {:id ::api}
   :middleware [;; query-params & form-params
                parameters/parameters-middleware
                  ;; content-negotiation
                muuntaja/format-negotiate-middleware
                  ;; encoding response body
                muuntaja/format-response-middleware
                  ;; exception handling
                coercion/coerce-exceptions-middleware
                  ;; decoding request body
                muuntaja/format-request-middleware
                  ;; coercing response bodys
                coercion/coerce-response-middleware
                  ;; coercing request parameters
                coercion/coerce-request-middleware
                  ;; exception handling
                exception/wrap-exception]})

;; Routes
(defn api-routes [_opts]
  [["/swagger.json"
    {:get {:no-doc  true
           :swagger {:info {:title "kanishk.guestbook API"}}
           :handler (swagger/create-swagger-handler)}}]
   ["/health" {:get health/healthcheck!}]
   ["/guest/message" {:post {:parameters {:body {:name string?, :message string?}}
                                  :handler (partial guestbook/create-message! _opts)}}]
   ["/guest/messages" {:get {:handler (partial guestbook/get-messages _opts)}}]
   ["/guest/message/:id" {:get {:parameters {:query {:id number?}}
                                :handler (partial guestbook/get-message _opts)}} 
    #_{:conflicting true}]
   ["/guest/update-message/:id" {:put {:parameters {:query {:id number?} :body {:name string? :message string?}}
                                :handler (partial guestbook/update-message! _opts)}} 
    #_{:conflicting true}]
   ["/guest" {:post {:parameters {:body {:username string?, :password string?}}
                             :handler (partial guests/create-guest! _opts)}}]
   ["/login" {:post {:parameters {:body {:username string?, :password string?}}
                         :handler (partial guests/set-user! _opts)}}]
   ["/logout" {:post {:parameters {:body {:user-id string?}}
                      :handler (partial guests/unset-user! _opts)}}]
   ["/logged-in" {:get {:handler (partial guests/logged-in? _opts)}}]])

(derive :reitit.routes/api :reitit/routes)

(defmethod ig/init-key :reitit.routes/api
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  (fn [] [base-path route-data (api-routes opts)]))
