(ns checklist.components.websocket-server
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]
            [taoensso.sente :as sente]
            [compojure.core :refer [GET POST routes]]
            [taoensso.sente.server-adapters.http-kit :refer (get-sch-adapter)]))

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

(defrecord WebsocketServer [ring-ajax-post ring-ajax-get-or-ws-handshake ch-chsk chsk-send! chsk-router options handler]
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


(defn new-websocket-server
  "Make a new websocket server, using `handler` to handle all received events"
  [& {:keys [handler options] :or {handler pinger options {:csrf-token-fn nil}}}] ; disable csrf for now
  (map->WebsocketServer {:options options :handler handler}))
