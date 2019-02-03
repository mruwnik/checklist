(ns checklist.events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx dispatch]]
            [taoensso.sente :as sente]
            [checklist.db :as db]))

(reg-event-db :initialize-db (fn  [_ _] db/default-db))

(reg-event-fx :ping (fn [{:keys [db]} [_ id]] {:ping id}))
(reg-event-fx :pushed (fn [{:keys [db]} [_ data]] (println data){}))


(defn register-chsk-send!
  "Setup the websocket sender,"
  [chsk-send!]
  (defn send [value]
    (chsk-send! [:checklist/check {:value value}] 5000
                (fn [reply]
                  (if (sente/cb-success? reply) ; Checks for :chsk/closed, :chsk/timeout, :chsk/error
                    (dispatch [:pushed reply])
                    (println "Could not send data to server:" reply)))))

  (reg-fx :ping (fn [value] (send value))))
