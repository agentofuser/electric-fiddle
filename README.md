# Electric Fiddle

This is how we fiddle around with stuff at work. All our demos are here.

## Quick Start

Begin with an example "Hello World" fiddle:

```shell
$ clj -A:dev
```
The REPL should print:
```clojure
;; => INFO  dev: {:host "0.0.0.0", :port 8080, :resources-path "public", :manifest-path "public/js/manifest.edn"}
;; => INFO  dev: Starting Electric compiler and server...
;; => shadow-cljs - nREPL server started on port 9001
;; => [:dev] Configuring build.
;; => [:dev] Compiling ...
;; => [:dev] Build completed. (231 files, 2 compiled, 0 warnings, 2.46s)
;; => INFO  electric-fiddle.server-jetty: 👉 http://0.0.0.0:8080
```

1. Navigate to [http://localhost:8080](http://localhost:8080)
2. Corresponding source code is in `src/hello_fiddle`

## Load more fiddles

In `electric-fiddle.edn`, add `electric-tutorial` under `:loaded-fiddles`:

```diff
 {:loaded-fiddles [hello-fiddle
+                  electric-tutorial ; requires :electric-tutorial alias and `npm install`
                   ]
 }
```

Restart your REPL with the required dependencies:
```shell
$ npm install
$ clj -A:dev:electric-tutorial
```

Navigate to [http://localhost:8080](http://localhost:8080) (or refresh your browser tab). The pages shows a new entry for `electric-tutorial`.

## Roll your own

1. `mkdir src/my_fiddle`
3. Add the following to `src/my_fiddle/fiddles.cljc`:
```clojure
(ns my-fiddle.fiddles
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]))

(e/defn MyFiddle []
  (e/client
    (dom/h1 (dom/text "Hello from my fiddle."))))

(e/def fiddles ; Entries for the dev index
  {`MyFiddle MyFiddle})

(e/defn FiddleMain [ring-req] ; prod entrypoint
  (e/server
    (binding [e/http-request ring-req])
      (e/client
        (binding [dom/node js/document.body]
          (MyFiddle.)))))
```

Add `my-fiddle` to `electric-fiddle.edn` > `:loaded-fiddles`.

If your fiddle requires extra dependencies:

- add them as an alias in `deps.edn`:

```clojure
{:aliases {:my-fiddle {:extra-deps {my.extra/dependency {:mvn/version "123"}}}}}
```

- Restart your REPL with the new alias: `$ clj -A:dev:my-fiddle`

# Prod build

Deploys one fiddle at a time.

## "Hello World" prod build

```shell
$ clojure -X:build:prod build-client :hyperfiddle/domain hello-fiddle # :debug false :verbose false :optimize true
$ clj -M:prod -m prod
```

## With extra dependencies
```shell
$ npm install
$ clojure -X:build:prod:electric-tutorial build-client :hyperfiddle/domain electric-tutorial
$ clj -M:prod:electric-tutorial -m prod
# http://localhost:8080/electric-tutorial.tutorial!%54utorial/electric-tutorial.demo-two-clocks!%54wo%43locks
```

# Uberjar

```shell
$ clojure -X:build:prod uberjar :hyperfiddle/domain hello-fiddle :build/jar-name "app.jar"
$ java -cp app.jar clojure.main -m prod
```

# Fly.io deployment

```shell
$ fly deploy --remote-only --config src/hello_fiddle/fly.toml
```
