(ns checklist.components.websocket-client
  (:require [com.stuartsierra.component :as component]
            [taoensso.sente  :as sente :refer (cb-success?)]))

(defmulti -event-msg-handler
  "Multimethod to handle Sente `event-msg`s"
  :id) ; Dispatch on event-id

(defn event-msg-handler
  "Wraps `-event-msg-handler` with logging, error catching, etc."
  [{:as ev-msg :keys [id ?data event]}]
  (-event-msg-handler ev-msg))

(defmethod -event-msg-handler
  :default ; Default/fallback case (no other matching handler)
  [{:as ev-msg :keys [event]}]
  (println "Unhandled event: %s" event))

(defmethod -event-msg-handler :chsk/state
  [{:as ev-msg :keys [?data]}]
  (println "Channel socket state change: %s" ?data))

(defmethod -event-msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (println  "Push event from server: %s" ?data))

(defmethod -event-msg-handler :chsk/handshake
  [{:as ev-msg :keys [?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]
    (println "Handshake: %s" ?data)))


(defrecord WebsocketClient [chsk ch-recv send-fn state router endpoint handler]
  component/Lifecycle
  (start [component]
    (let [{:keys [chsk ch-recv send-fn state]}
          (sente/make-channel-socket! "/chsk" "" {:type :ws})]
      (assoc component
             :chsk chsk
             :ch-chsk ch-recv ; ChannelSocket's receive channel
             :chsk-send! send-fn ; ChannelSocket's send API fn
             :chsk-state state   ; Watchable, read-only atom
             :router (sente/start-client-chsk-router! ch-recv handler)
             )))
  (stop [component]
    (println (keys component))
    component))

(defn new-websocket-client [& {:keys [handler endpoint]
                               :or {handler event-msg-handler endpoint "/chsk"}}]
  (map->WebsocketClient {:endpoint endpoint :handler handler}))
