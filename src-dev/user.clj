(ns user
  (:require fiddle-manager))

(fiddle-manager/write-loader-file "./src-dev/fiddles.cljc")
(require 'dev) ; Under :dev alias, automatically load 'dev so the REPL is ready to go with zero interaction
(future (dev/-main)) ; start system. Wrapped in future to not block user REPL
