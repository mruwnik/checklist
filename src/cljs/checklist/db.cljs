(ns checklist.db)

(def default-db
  {:name "re-frame"
   :checklist [
               {:name "root 1"
                :id 1
                :state :checked
                :items [
                        {:name "item1" :state :unchecked :id 2}
                        {:name "item2" :state :checked :id 3}
                        {:name "item3"
                         :state :unchecked :id 4
                         :items [
                                    {:name "nested item1" :state :unchecked :id 5}
                                    {:name "nested item2" :state :checked :id 6}
                                    {:name "nested item3" :state :unchecked :id 7}
                                    {:name "nested item4" :state :unchecked :id 8}
                                    {:name "nested item5" :state :unchecked :id 9}]}]}
               {:name "root 2"
                :state :checked :id 10
                :items [
                           {:name "item4"
                            :state :unchecked :id 11
                            :items [
                                    {:name "nested item6" :state :unchecked :id 12}
                                    {:name "nested item7" :state :checked :id 13}
                                    {:name "nested item8" :state :unchecked :id 14}
                                    {:name "nested item9" :state :unchecked :id 15}]}
                        {:name "item5" :state :checked :id 16}
                        {:name "item6" :state :unchecked :id 17}]}]})
