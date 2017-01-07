(defproject web-demo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [ring "1.5.0"]
                 [hiccup "1.0.5"]
                 [calfpath "0.4.0"]
                 [couchbase-clj "0.2.0"]]

  :main ^:skip-aot web-demo.core
  :ring {:handler web-demo.core/reloadable-app}
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
