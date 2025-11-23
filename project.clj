(defproject babashka/fs "0.5.28"
  :description "Babashka file system utilities."
  :url "https://github.com/babashka/fs"
  :scm {:name "git"
        :url "https://github.com/babashka/fs"}
  :license {:name "Eclipse Public License 1.0"
            :url "http://opensource.org/licenses/eclipse-1.0.php"}
  :source-paths ["src"]
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :plugins [[lein-cloverage "1.2.4"]]
  :profiles {:test {:jvm-opts ["-Djdk.attach.allowAttachSelf"]
                    :dependencies [[clj-commons/conch "0.9.2"]]}}
  :deploy-repositories [["clojars" {:url "https://clojars.org/repo"
                                    :username :env/clojars_user
                                    :password :env/clojars_pass
                                    :sign-releases false}]])
