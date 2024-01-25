(ns dustingetz.de.de-join-internal
  (:require [missionary.core :as m]
            [hyperfiddle.incseq :as i]
            [hyperfiddle.electric.impl.runtime-de :as r]))

(defn on-diff! [cb incseq]
  ((m/reduce (fn [_ d] (cb d) nil) nil incseq)
   cb (fn [e] (.printStackTrace ^Throwable e))))

(def !animals
  (atom [{:name "betsy" :owner "brian" :kind "cow"}
         {:name "jake" :owner "brian" :kind "horse"}
         {:name "josie" :owner "dawn" :kind "cow"}]))
(def !personalities
  (atom [{:kind "cow" :personality "stoic"}
         {:kind "horse" :personality "skittish"}]))

(def ps
  (on-diff! prn
    (r/root-frame ; incseq
      {::Main [(r/cdef 0 [nil nil nil] [nil] nil ; root entrypoint, called by convention
                 (fn [frame]
                   (r/define-node frame 0 (r/pure #{:kind})) ; ks
                   (r/define-node frame 1 (i/diff-by identity (m/watch !animals))) ; xs
                   (r/define-node frame 2 (i/diff-by identity (m/watch !personalities))) ; ys
                   (r/define-call frame 0 ; cursor frame
                     (r/ap (r/pure r/bind-args) ; x, y from cursor
                       (r/pure (doto (r/make-ctor frame ::Main 1) ; boot cursor body
                                 (r/define-free 0 (r/node frame 0)))) ; closure over ks
                       (r/singletons (r/node frame 1)) ; xs
                       (r/singletons (r/node frame 2)))) ; ys
                   (r/join (r/call frame 0))))
               (r/cdef 1 [nil nil nil] [nil] nil  ; cursor body
                 (fn [frame]
                   (r/define-node frame 0 (r/lookup frame 0)) ; x from cursor
                   (r/define-node frame 1 (r/lookup frame 1)) ; y from cursor
                   (r/define-node frame 2 (r/free frame 0)) ; lookup free ks and proxy
                   (r/define-call frame 0 ; cursor body continuation
                     (r/ap (r/pure {false (r/make-ctor frame ::Main 2) ; boot if false body
                                    true  (doto (r/make-ctor frame ::Main 3) ; boot if true body
                                            (r/define-free 0 (r/node frame 0)) ; x
                                            (r/define-free 1 (r/node frame 1)))}) ; y
                       (r/ap (r/pure =)
                         (r/ap (r/pure select-keys) (r/node frame 0) (r/node frame 2)) ; (select-keys x ks)
                         (r/ap (r/pure select-keys) (r/node frame 1) (r/node frame 2))))) ; (select-keys y ks)
                   (r/join (r/call frame 0))))
               (r/cdef 0 [] [] nil ; if false body
                 (fn [frame]
                   (r/pure))) ; (e/amb)
               (r/cdef 2 [] [] nil ; if true body
                 (fn [frame]
                   (r/ap (r/pure merge) (r/free frame 0) (r/free frame 1))))]} ; (merge x y)
      ::Main)))

(comment
  {:degree 3, :permutation {}, :grow 3, :shrink 0, :freeze #{},
   :change {0 {:name "betsy", :owner "brian", :kind "cow", :personality "stoic"},
            1 {:name "jake", :owner "brian", :kind "horse", :personality "skittish"},
            2 {:name "josie", :owner "dawn", :kind "cow", :personality "stoic"}}}

  (swap! !animals conj {:name "bob" :owner "jack" :kind "horse"})
  {:degree 4, :permutation {}, :grow 1, :shrink 0, :freeze #{},
   :change {3 {:name "bob", :owner "jack", :kind "horse", :personality "skittish"}}}
  (swap! !animals pop)
  {:degree 4, :permutation {}, :grow 0, :shrink 1, :change {}, :freeze #{}})