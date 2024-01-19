(ns dustingetz.differential-fizzbuzz
  (:require [hyperfiddle.electric :as e]))

(def !n (atom 10))
(def !fizz (atom "Fizz"))
(def !buzz (atom "Buzz"))

(e/defn RangeN [n] (e/diff-by identity (range 1 (inc n))))

(e/defn App []
  (let [fizz (e/watch !fizz) ; i/fixed + m/watch + e/join
        buzz (e/watch !buzz)]

    (let [is (RangeN. (e/watch !n)) ; variable in time and space
          results (e/cursor [i is]
                    (cond
                      (zero? (mod i (* 3 5))) (str fizz buzz)
                      (zero? (mod i 3)) fizz
                      (zero? (mod i 5)) buzz
                      :else i))]

      ; these are the same!
      (println results) ; println product
      (e/cursor [r results] (println r)) ; println element-wise
      )))



(vector is is) ; vector of size n * n