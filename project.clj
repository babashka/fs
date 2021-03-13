(defproject babashka/fs "0.0.2"
  :description "Babashka file system utilities."
  :url "https://github.com/babashka/fs"
  :scm {:name "git"
        :url "https://github.com/babashka/fs"}
  :license {:name "Eclipse Public License 1.0"
            :url "http://opensource.org/licenses/eclipse-1.0.php"}
  :source-paths ["src"]
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :plugins [[lein-codox "0.10.7"]
            [lein-cloverage "1.2.2"]]
  :codox {:output-path "gh-pages"}
  :profiles {:test {:jvm-opts ["-Djdk.attach.allowAttachSelf"]
                    :dependencies [[clj-commons/conch "0.9.2"]]}}
  :deploy-repositories [["clojars" {:url "https://clojars.org/repo"
                                    :username :env/clojars_user
                                    :password :env/clojars_pass
                                    :sign-releases false}]])

