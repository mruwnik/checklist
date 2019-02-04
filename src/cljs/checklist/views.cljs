(ns checklist.views
  (:require [re-frame.core :as re-frame]
            [clojure.string :as str])
  (:require-macros [checklist.resources :refer [svg-icon]]))

(defn icon [name]
  (name {:unchecked (svg-icon "unchecked")
         :checked (svg-icon "check")
         :blocked (svg-icon "ban")
         :warn (svg-icon "warning")
         :target (svg-icon "target")
         :protect (svg-icon "shield")
         :question (svg-icon "question-mark")}))


(defn classes [& names]
  (str/join " " (filter identity names)))


(defn details [summary content]
  [:details {:open true} [:summary summary] content])


(defn checklist-item [{name :name state :state id :id}]
  [:span {:on-click #(re-frame.core/dispatch [:item-checked id])}
   (icon state) name])

(defn list-item [{name :name state :state items :items :as item}]
  [:div {:class (classes "item" state)}
   (if (seq items)
     (details [:span (icon state) name] (into [:div {:class "children"}] (map list-item items)))
     (checklist-item item))])


(defn checklist []
  (let [items (re-frame/subscribe [:checklist])]
    (into [:div] (map list-item @items))))


(defn main-panel []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:div "Hello from " @name
       [checklist]
       [:button {:on-click #(re-frame.core/dispatch [:ping :me])} "asd"]])))
