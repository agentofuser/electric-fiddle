(ns dustingetz.hfql-teeshirt-orders
  (:require [hyperfiddle.api :as hf]
            [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [hyperfiddle.hfql :refer [hfql]]
            [hyperfiddle.hfql-tree-grid :refer [with-gridsheet-renderer]]
            #?(:clj [user.orders-datomic :refer [orders genders shirt-sizes]])))

(e/defn Teeshirt-orders []
  (hfql [hf/*$* hf/db]
    {(orders .)
     [:db/id
      :order/email
      (props :order/gender {::hf/options (genders)
                            ::hf/option-label (e/fn [x] (name x))})
      (props :order/shirt-size {::hf/options (shirt-sizes order/gender .) 
                                ::hf/option-label (e/fn [x] (name x))})]}))

(e/defn Webview-HFQL [_]
  (e/client
    (with-gridsheet-renderer
      (e/server
        (binding [hf/db user.orders-datomic/*$* ; hfql compiler
                  hf/*nav!*   user.orders-datomic/nav! ; hfql compiler
                  hf/*schema* user.orders-datomic/schema ; hfql gridsheet renderer
                  ]
          (Teeshirt-orders.))))))
