(ns dustingetz.painter-differential
  (:require #?(:cljs goog.dom)
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.incseq :as i]
            [missionary.core :as m]))

#?(:cljs
   (defn mount-items [element {:keys [grow shrink degree permutation change]}]
     (let [children (.-childNodes element)
           move (i/inverse permutation)
           size-before (- degree grow)
           size-after (- degree shrink)]
       (loop [i size-before
              c change]
         (if (== i degree)
           (reduce-kv
             (fn [_ i e]
               (.replaceChild element e
                 (.item children (move i i))))
             nil c)
           (let [j (move i i)]
             (.appendChild element (c j))
             (recur (inc i) (dissoc c j)))))
       (loop [p permutation
              i degree]
         (if (== i size-after)
           (loop [p p]
             (when-not (= p {})
               (let [[i j] (first p)]
                 (.insertBefore element (.item children j)
                   (.item children (if (< j i) (inc i) i)))
                 (recur (i/compose p (i/rotation i j))))))
           (let [i (dec i)
                 j (p i i)]
             (.removeChild element (.item children j))
             (recur (i/compose p (i/rotation i j)) i))))
       element)))

#?(:cljs (def mousedown (atom false)))
#?(:clj (defonce !xs (i/spine)))

(def auto-inc (partial swap! (atom 0) inc))
#?(:clj (defn add! [!xs c] (!xs (auto-inc) {} c)))

(e/defn Painter []
  (e/client
    (dom/h1 (dom/text "hello"))
    #_(dom/div

        (dom/style {:width "100vw" :height "100vh"
                    :word-break "break-all"})

        (dom/on "mousedown" (e/fn [e] (reset! mousedown true)))
        (dom/on "mouseup" (e/fn [e] (reset! mousedown false)))

        (dom/on "mousemove"
          (e/fn [e]
            (when (e/watch mousedown)
              (e/server
                (add! !xs "X")))))

        #_(new
            (m/reductions mount-items dom/node
              (i/latest-product
                (fn [c] (goog.dom/createTextNode c))
                (e/server (new (identity !xs))))))

        #_(e/for-by identity [e (e/server !xs)]
            (dom/text e)))))