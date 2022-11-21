(ns repl-sessions.classpath-poke
  (:require [lambdaisland.classpath :as licp]
            [clojure.java.io :as io]))

;; Started a REPL without any aliases, so there's no `my_app/env_config.clj` on
;; the classpath yet.

(require 'my-app.env-config)
;; => Unhandled java.io.FileNotFoundException

(io/resource "my_app/env_config.clj")
;; => nil

;; Now let's add an alias

(licp/update-classpath! {:aliases [:dev]})

;; Now `env/dev` is on the classpath, so clojure can resolve the file.

(io/resource "my_app/env_config.clj")
;; => #object[java.net.URL 0x663d8e1f "file:env/dev/my_app/env_config.clj"]

(require 'my-app.env-config)

my-app.env-config/env
;; => :dev

;; You can also see it when you inspect the chain, the priortyclassloader comes
;; higher up in the chain, and will (contrary to other class loaders) check its
;; own sources first before delegating to its parent.

(licp/classpath-chain)
;; =>
(;; A bunch of DynamicClassLoader because of a long standing nREPL bug
 [clojure.lang.DynamicClassLoader@4bebf38f ()]
 ,,,
 ;; The priority class loader with the extra path
 [lambdaisland/priority-classloader14365 ("file:env/dev/")]
 ;; The classloader that Clojure mostly uses
 [clojure.lang.DynamicClassLoader@4020f974 ()]
 ;; The classpath entries that we booted up with
 [app
  ("src"
   "/home/arne/.m2/repository/org/clojure/clojure/1.11.1/clojure-1.11.1.jar"
   ,,,,
   )]
 [platform ()])

;; We can try the other one
(licp/update-classpath! {:aliases [:test]})

(io/resource "my_app/env_config.clj")
;; => #object[java.net.URL 0x5c3a1d17 "file:env/test/my_app/env_config.clj"]

(require 'my-app.env-config :reload)

my-app.env-config/env
;; => :test

;; In this case we don't really have to go through `licp/update-classpath!`, it
;; does expensive dependency resolution via tools.deps and then compares that to
;; the classpath at boot time. If we know the path we want to add, we can use
;; `install-priority-loader` directly (which will replace any existing priority
;; loader)

(licp/install-priority-loader! ["env/dev"])

(licp/classpath-chain)
;;=>
([clojure.lang.DynamicClassLoader@367ecd75 ()]
 [lambdaisland/priority-classloader14406 ("file:env/dev/")]
 [clojure.lang.DynamicClassLoader@4020f974 ()]
 [app
  ("src"
   "/home/arne/.m2/repository/org/clojure/clojure/1.11.1/clojure-1.11.1.jar"
   ,,,
   )]
 [platform ()])
