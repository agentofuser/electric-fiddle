(ns keykapp.fiddles
  (:import [hyperfiddle.electric Pending])
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

#?(:clj (defonce !keychanges (atom (list))))
(e/def keychanges (e/server (e/watch !keychanges)))

(defn keychange-from [event]
  (let [code (.-code event)
        key (.-key event)
        is-repeat (.-repeat event)
        event-type (.-type event)
        keychange {:event-type event-type :code code :key key :is-repeat is-repeat}]
    keychange))

(e/defn log-keychange [event] (let [performance-timestamp (e/client (+ (.-timeOrigin js/performance) (. js/performance now)))
                                    keychange (merge (keychange-from event) {:performance-timestamp performance-timestamp})]
                                (println keychange)
                                (e/server (swap! !keychanges #(cons keychange %)))))

(e/defn Keykapp []
  (e/client
    (dom/h1 (dom/text "Hello from my fiddle."))
    (dom/on "keydown" log-keychange)
    (dom/on "keyup" log-keychange)

    (try
      (dom/ul
        (e/server
          (e/for-by identity [keychange (reverse (take 9 keychanges))]
            (e/client
              (dom/li
                (dom/text keychange))))))

      (catch Pending e
        (dom/style {:background-color "yellow"})))))

(e/def fiddles ; Entries for the dev index
  {`Keykapp Keykapp})

(e/defn FiddleMain [ring-req] ; prod entrypoint
  (e/server
    (binding [e/http-request ring-req])
    (e/client
      (binding [dom/node js/document.body]
        (Keykapp.)))))