(ns checklist.effects
  (:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx dispatch]]
            [taoensso.sente :as sente]
            [checklist.db :as db]))



(defn register-chsk-send!
  "Setup the websocket sender,"
  [chsk-send!]
  (defn send [value]
    (chsk-send! [:checklist/check {:value value}] 5000
                (fn [reply]
                  (if (sente/cb-success? reply) ; Checks for :chsk/closed, :chsk/timeout, :chsk/error
                    (dispatch [:pushed reply])
                    (println "Could not send data to server:" reply)))))

  (reg-fx :ping (fn [value] (send value)))
  (reg-fx :changes (fn [changes] (send changes))))
