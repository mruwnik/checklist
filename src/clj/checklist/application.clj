(ns checklist.application
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [checklist.components.server-info :refer [server-info]]
            [checklist.components.websocket-server :refer [new-websocket-server sente-routes]]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [system.components.middleware :refer [new-middleware]]
            [system.components.http-kit :refer [new-web-server]]
            [checklist.config :refer [config]]
            [checklist.routes :refer [home-routes]]))


(defn app-system [config]
  (component/system-map
   :routes     (new-endpoint home-routes)
   :middleware (new-middleware {:middleware (:middleware config)})
   :handler    (-> (new-handler)
                   (component/using [:sente-endpoint :routes :middleware]))
   :http       (-> (new-web-server (:http-port config))
                   (component/using [:handler]))
   :sente (new-websocket-server)
   :sente-endpoint (component/using (new-endpoint sente-routes) [:sente])
   :server-info (server-info (:http-port config))
   ))

(defn -main [& _]
  (let [config (config)]
    (-> config
        app-system
        component/start)))
