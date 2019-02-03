(ns checklist.system
  (:require [com.stuartsierra.component :as component]
            [checklist.components.ui :refer [new-ui-component]]
            [checklist.components.websocket-client :refer [new-websocket-client]]))

(declare system)

(defn new-system []
  (component/system-map
   :sente (new-websocket-client)
   :app-root (component/using (new-ui-component) [:sente])
   ))

(defn init []
  (set! system (new-system)))

(defn start []
  (set! system (component/start system)))

(defn stop []
  (set! system (component/stop system)))

(defn ^:export go []
  (init)
  (start))

(defn reset []
  (stop)
  (go))
