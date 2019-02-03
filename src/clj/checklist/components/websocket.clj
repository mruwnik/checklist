(ns checklist.components.websocket
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]
            [taoensso.sente :as sente]
            [compojure.core :refer [GET POST routes]]
            [taoensso.sente.server-adapters.http-kit :refer (get-sch-adapter)]))


;; (defmulti -event-msg-handler
;;   "Multimethod to handle Sente `event-msg`s"
;;   :id) ; Dispatch on event-id

;; (defn event-msg-handler
;;   "Wraps `-event-msg-handler` with logging, error catching, etc."
;;   [{:as ev-msg :keys [id ?data event]}]
;;   (-event-msg-handler ev-msg))


;; (defmethod -event-msg-handler
;;   :default ; Default/fallback case (no other matching handler)
;;   [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
;;   (let [session (:session ring-req)
;;         uid (:uid session)]
;;     (println "Unhandled event: %s" event)
;;     (when ?reply-fn
;;       (?reply-fn {:umatched-event-as-echoed-from-from-server event}))))

;; (defmethod -event-msg-handler :checklist/
;;   [{:as ev-msg :keys [?reply-fn]}]
;;   (let [loop-enabled? (swap! broadcast-enabled?_ not)]
;;     (?reply-fn loop-enabled?)))


(defn pinger
  "Default websocket handler - log the request and return to sender"
  [{:as ev-msg :keys [id ?data event ?reply-fn]}]
  (log/info "Unhandled event: %s" event)
  (when ?reply-fn
    (?reply-fn {:pong event})))


(defn sente-routes
  "Handle initial websocket handshake. Adds /chsk routes."
  [{{ring-ajax-post :ring-ajax-post ring-ajax-get-or-ws-handshake :ring-ajax-get-or-ws-handshake} :sente}]
  (routes
   (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
   (POST "/chsk" req (ring-ajax-post req))))


(defrecord Websocket [ring-ajax-post ring-ajax-get-or-ws-handshake ch-chsk chsk-send! chsk-router options handler]
  component/Lifecycle
  (start [component]
      (let [{:keys [ch-recv send-fn connected-uids
                    ajax-post-fn ajax-get-or-ws-handshake-fn]}
            (sente/make-channel-socket-server! (get-sch-adapter) options)]
        (assoc component
               :ring-ajax-post ajax-post-fn
               :ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn
               :ch-chsk ch-recv
               :chsk-send! send-fn
               :connected-uids connected-uids
               :chsk-router (sente/start-server-chsk-router! ch-recv handler))))
  (stop [component]
    component))


(defn new-websocket [& {:keys [handler options] :or {handler pinger options {}}}]
  (map->Websocket {:options options :handler handler}))
