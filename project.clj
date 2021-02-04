(defproject krapi.clj "0.1.0"
  :description "Clojure library to access Kraken.com REST API"
  :url "https://github.com/ali-raheem/krapi.clj"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clj-http "3.12.0"]                                         
                 [cheshire "5.10.0"]                                         
                 [pandect "1.0.1"]]
  :repl-options {:init-ns krapi.clj})
