(defproject emailz "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [buddy/buddy-sign "1.5.0"]
                 [hiccup "1.0.5"]
                 [clojurewerkz/mailer "1.3.0"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler emailz.handler/app
         :init emailz.startup/init
         :nrepl {:start? true
                 :port 50505}}
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                  [cider/cider-nrepl "0.15.0-SNAPSHOT"]
                                  [refactor-nrepl "2.4.0-SNAPSHOT"]
                                  [ring/ring-mock "0.2.0"]]
                   :repl-options {:nrepl-middleware
                                  [cider.nrepl.middleware.apropos/wrap-apropos
                                   cider.nrepl.middleware.classpath/wrap-classpath
                                   cider.nrepl.middleware.complete/wrap-complete
                                   cider.nrepl.middleware.debug/wrap-debug
                                   cider.nrepl.middleware.format/wrap-format
                                   cider.nrepl.middleware.info/wrap-info
                                   cider.nrepl.middleware.inspect/wrap-inspect
                                   cider.nrepl.middleware.macroexpand/wrap-macroexpand
                                   cider.nrepl.middleware.ns/wrap-ns
                                   cider.nrepl.middleware.spec/wrap-spec
                                   cider.nrepl.middleware.pprint/wrap-pprint
                                   cider.nrepl.middleware.pprint/wrap-pprint-fn
                                   cider.nrepl.middleware.refresh/wrap-refresh
                                   cider.nrepl.middleware.resource/wrap-resource
                                   cider.nrepl.middleware.stacktrace/wrap-stacktrace
                                   cider.nrepl.middleware.test/wrap-test
                                   cider.nrepl.middleware.trace/wrap-trace
                                   cider.nrepl.middleware.out/wrap-out
                                   cider.nrepl.middleware.undef/wrap-undef
                                   cider.nrepl.middleware.version/wrap-version
                                   refactor-nrepl.middleware/wrap-refactor]}}})
