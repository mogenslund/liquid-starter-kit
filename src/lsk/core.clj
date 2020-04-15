(ns lsk.core
  (:require [clojure.string :as str]
            [liq.core :as liq-core]
            [liq.buffer :as buffer]
            [liq.editor :as editor]
            [liq.modes.typeahead-mode :as typeahead-mode])
  (:import [com.google.gson GsonBuilder JsonParser]))

(defn pretty-json
  [text]
  (let [gson (-> (GsonBuilder.) .setPrettyPrinting .create)
        jp (JsonParser.)]
    (.toJson gson (.parse jp text))))

(defn pretty-json-selection
  [buf]
  (let [text (buffer/get-selected-text buf)
        json (pretty-json text)]
    (if (not= text json)
      (-> buf
          buffer/delete
          (buffer/insert-string json)) 
      buf)))

(defn my-typeahead
  []
  (typeahead-mode/run ["apple" "ananas" "pineapple" "grape" "kiwi"]
    str
    #(editor/apply-to-buffer (fn [buf] (buffer/insert-string buf %)))))

(defn -main
  [& args]
  (apply liq-core/-main args)

  (swap! editor/state
          assoc-in [::editor/modes :fundamental-mode :normal "f7"]
          #(editor/apply-to-buffer pretty-json-selection))

  (swap! editor/state assoc-in [::editor/commands :my-typeahead] my-typeahead)
  (swap! editor/state assoc-in [::editor/modes :fundamental-mode :normal "f8"] :my-typeahead))

