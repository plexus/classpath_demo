(ns repl-sessions.classpath-poke-2
  (:require [lambdaisland.classpath :as licp]
            [clojure.java.io :as io]))

;; This time I've started Clojure with -A:dev, so the dev resources are already
;; on the classpath.

(licp/classpath-chain)
;;=>
([clojure.lang.DynamicClassLoader@231ececa ()]
 ,,,
 [app
  ("env/dev"
   "src"
   "/home/arne/.m2/repository/org/clojure/clojure/1.11.1/clojure-1.11.1.jar"
   ,,,)]
 [platform ()])

;; So it can load the env_config.clj from there

(require 'my-app.env-config)

my-app.env-config/env
;; => :dev

;; But if I want `test` to take precedence than I can

;; All three ultimately have the same effect, a priority-classloade with
;; `env/test`
(licp/install-priority-loader! ["env/test"])
(licp/update-classpath! {:extra {:paths ["env/test"]}})
(licp/update-classpath! {:aliases [:test]})

(licp/classpath-chain)
([clojure.lang.DynamicClassLoader@67743966 ()]
 ,,,
 [lambdaisland/priority-classloader14380 ("file:env/test/")]
 [clojure.lang.DynamicClassLoader@4020f974 ()]
 [app
  ("env/dev"
   "src"
   "/home/arne/.m2/repository/org/clojure/clojure/1.11.1/clojure-1.11.1.jar"
   ,,,)]
 [platform ()])

;; and indeed that's where clojure now looks for things first

(io/resource "my_app/env_config.clj")
;; => #object[java.net.URL 0x1742e99c "file:env/test/my_app/env_config.clj"]

(require 'my-app.env-config :reload)

my-app.env-config/env
;; => :test
