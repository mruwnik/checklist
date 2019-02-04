(ns checklist.resources
  (:require [clojure.xml :as xml]
            [clojure.java.io :refer (resource file)]))


(defn to-tag [{tag :tag attrs :attrs content :content}]
  (into [tag attrs] (when content (map to-tag content))))

(defmacro svg-icon
  [icon-name]
  (-> (str "public/open-iconic/svg/" icon-name ".svg")
      resource file xml/parse
     to-tag
     (update 1 assoc :class "icon")))
