(ns dustingetz.de.de-join
  (:require [hyperfiddle.electric :as e]))

(e/defn Join [ks xs ys]
  (e/cursor [x xs, y ys]
    (if (= (select-keys x ks) (select-keys y ks))
      (merge x y) (e/amb))))

(let [animals
      (e/amb
        {:name "betsy" :owner "brian" :kind "cow"}
        {:name "jake"  :owner "brian" :kind "horse"}
        {:name "josie" :owner "dawn"  :kind "cow"})
      personalities
      (e/amb
        {:kind "cow"   :personality "stoic"}
        {:kind "horse" :personality "skittish"})]
  (e/as-vec (Join. #{:kind} animals personalities)))

[{:name "betsy", :owner "brian", :kind "cow"  , :personality "stoic"}
 {:name "jake" , :owner "brian", :kind "horse", :personality "skittish"}
 {:name "josie", :owner "dawn" , :kind "cow"  , :personality "stoic"}]