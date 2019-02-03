(ns checklist.components.ui
  (:require [com.stuartsierra.component :as component]
            [checklist.core :refer [render]]))

(defrecord UIComponent [sente]
  component/Lifecycle
  (start [component]
    (render (:chsk-send! sente))
    component)
  (stop [component]
    component))

(defn new-ui-component []
  (map->UIComponent {}))
