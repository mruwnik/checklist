(ns checklist.events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx reg-fx dispatch]]
            [taoensso.sente :as sente]
            [checklist.db :as db]))

(defn checklist-state [state]
  (condp = state
    :unchecked :checked
    :checked :unchecked
    :unchecked))


;; Handle checking/unchecking list items.
;;
;; This is so complicated because of the nested nature of the way that items
;; are represented.

(defn path-to-item
  "Get the path to the item that has the given `item-id` in the given `items`"
  [items item-id]
  (if-let [pos (first (keep-indexed #(if (= (:id %2) item-id) %1) items))]
    [pos]
    (->> items
         (map #(path-to-item (:items %) item-id))
         (keep-indexed #(if (seq %2) (into [%1 :items] %2)))
         first)))

(defn set-item-val [db path key val]
  (assoc-in db (concat [:checklist] path [key]) val))
(defn get-item-val [db path key] (get-in db (concat [:checklist] path [key])))


(defn all-children-selected? [db path]
  (every? #(= (:state %) :checked) (get-item-val db path :items)))

(defn should-change
  "Return whether the item state at the given `path` should be changed if for the given `state`"
  [db path state]
  (cond
    (empty? path) nil                                                      ; There is no item to be checked - obviously it shouldn't be changed
    (= (get-item-val db path :state) state) nil                     ; The current state is the same as the one being set - there's no need to change
    (nil? (get-item-val db path :items)) true                       ; The current item has no children - therefore this is the item actually selected, so change it
    (and (= state :checked) (all-children-selected? db path)) true  ; All children are selected - this item should also be selected
    (and (not= state :checked) (not (all-children-selected? db path))) true)) ; not all children are selected - deselect this item

(defn bubble-changes
  "Go through all items and change the state of those that should be updated.

  This returns a map of {:db <the current react state> :changes <a list of all changes made>}"
  ([db path new-state] (bubble-changes db path new-state []))
  ([db path new-state changes]
   (if-not (should-change db path new-state)
     {:db db :changes changes}
     (bubble-changes
      (set-item-val db path :state new-state)
      (drop-last 2 path)
      new-state
      (conj changes {:id (get-item-val db path :id) :state new-state})))))


(defn check-item
  "Check the given item off the list"
  [{:keys [db]} [_ item-id]]
  (let [path (path-to-item (:checklist db) item-id)
        new-state (->> :state (get-item-val db path) checklist-state)]
    (bubble-changes db path new-state)))




(reg-event-db :initialize-db (fn  [_ _] db/default-db))

(reg-event-fx :item-checked check-item)



(reg-event-fx :ping (fn [{:keys [db]} [_ id]] {:ping id}))
(reg-event-fx :pushed (fn [{:keys [db]} [_ data]] (println data){}))
