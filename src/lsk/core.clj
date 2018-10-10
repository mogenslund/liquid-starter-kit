(ns lsk.core
  (:require [clojure.string :as str]
            [dk.salza.liq.core :as liq-core]
            [dk.salza.liq.editor :as editor]
            [dk.salza.additives.blob :refer :all])
  (:import [com.google.gson GsonBuilder JsonParser]))

(defn pretty-json
  [text]
  (let [gson (-> (GsonBuilder.) .setPrettyPrinting .create)
        jp (JsonParser.)]
    (.toJson gson (.parse jp text))))

(defn pretty-json-selection
  []
  (let [text (editor/get-selection)
        json (pretty-json text)]
    (editor/delete-selection)
    (editor/insert json)))
  
(defn mytypeahead
  []
  (editor/typeahead (list "aaa" "bbb" "ccc") str editor/insert))

(defn -main
  [& args]
  ;; Initialize
  (liq-core/set-defaults)
  (apply liq-core/startup args)
  (liq-core/init-editor)

  ;; Global keybindings
  (editor/set-global-key :f5 pretty-json-selection)

  ;; Typeahead C-space functions
  (editor/add-interactive "Pretty json" pretty-json-selection)
  (editor/add-interactive "My typeahead" mytypeahead)

  ;; Search paths for typeahead
  (editor/add-searchpath "/tmp")

  ;; Snippets for typeahead
  (editor/add-snippet "{topic: \"json\", why: \"Because I can\"}"))


;; Make functions available directly in editor
(ns user
  (:require [dk.salza.liq.tools.cshell :refer :all]
            [dk.salza.additives.blob :refer :all]))