(ns dustingetz.clojurenyc2024
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

#?(:clj
   (defn jvm-system-properties [?s]
     (->> (System/getProperties)
       (filter (fn [^java.util.concurrent.ConcurrentHashMap$MapEntry kv]
                 (clojure.string/includes? (clojure.string/lower-case (str (key kv)))
                   (clojure.string/lower-case (str ?s)))))
       (sort-by key))))

(e/defn Scratch []
  (e/server
    (e/for-by key [[k v] (jvm-system-properties "java")]
      (if (e/watch !q)
        (e/client
          (dom/tr (dom/text k)))))))
