(ns keykapp.fiddles
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

(defn log-key-event [event]
  (let [code (.-code event)
        key (.-key event)
        key (.-key event)
        is-repeat (.-repeat event)
        event-type (.-type event)]
    (cond
      (= event-type "keydown") (println {:event-type event-type :code code :key key :is-repeat is-repeat})
      (= event-type "keyup") (println {:event-type event-type :code code :key key}))))

(e/defn Keykapp []
  (e/client
    (dom/h1 (dom/text "Hello from my fiddle."))
       ;; Keydown event listener
    (dom/on "keydown" (e/fn [event] (log-key-event event)))
      ;; Keyup event listener
    (dom/on "keyup" (e/fn [event] (log-key-event event)))))

(e/def fiddles ; Entries for the dev index
  {`Keykapp Keykapp})

(e/defn FiddleMain [ring-req] ; prod entrypoint
  (e/server
    (binding [e/http-request ring-req])
    (e/client
      (binding [dom/node js/document.body]
        (Keykapp.)))))