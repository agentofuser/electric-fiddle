(ns dustingetz.ui-optimistic
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

(e/defn Form []
  (e/client
    (let [xs (e/server (e/diff-by key (jvm-system-properties "")))
          ys (e/client (e/diff-by key optimistic-client-records))]
      (e/cursor [[k v] (e/amb xs ys)]
        (Render-field. k v)))))
