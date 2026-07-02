;; Regenerates index.mjs (the JS API) from script/index.template.mjs by importing
;; the compiled module at runtime, reading its exports, and demunging the squint
;; names back to their Clojure names. Every function is wrapped with jsFriendly so
;; its option keys accept camelCase. Run via nbb: `bb gen-index` (builds lib first).

(require '["../lib/babashka/fs.mjs" :as fs]
         '[clojure.string :as str])

(def names
  (->> (js/Object.keys fs)
       (remove #{"with_temp_dir"}) ; compile-time macro, not JS-callable
       vec))

(defn value? [n] (not (fn? (aget fs n))))

(def overrides
  {"sym-link?" "isSymlink"
   "read-attributes*" "readAttributesRaw"
   "exists?" "exists"
   "starts-with?" "startsWith"
   "ends-with?" "endsWith"})

(defn camel [clj]
  (or (overrides clj)
      (let [pred (str/ends-with? clj "?")
            base (-> clj (str/replace "?" "") (str/replace "->" "-to-") (str/replace "*" ""))
            parts (remove str/blank? (str/split base #"-"))
            cc (apply str (first parts) (map str/capitalize (rest parts)))]
        (if pred
          (str "is" (str/upper-case (subs cc 0 1)) (subs cc 1))
          cc))))

(def reserved
  #{"delete" "new" "class" "return" "typeof" "in" "for" "do" "if" "void" "yield"})

(def rows (mapv (fn [n] [n (camel (demunge n))]) names))

(when-not (apply distinct? (map second rows))
  (throw (js/Error. (str "duplicate JS name: "
                         (->> (map second rows) frequencies (filter (fn [[_ v]] (< 1 v))))))))

(defn wrap [n] (if (value? n) (str "raw." n) (str "jsFriendly(raw." n ")")))

(def consts
  (str/join "\n"
            (map (fn [[n c]]
                   (if (reserved c)
                     (str "const " c "_ = " (wrap n) ";")
                     (str "const " c " = " (wrap n) ";")))
                 rows)))

(def exports
  (str/join ",\n"
            (map (fn [[_ c]]
                   (if (reserved c)
                     (str "  " c "_ as " c)
                     (str "  " c)))
                 rows)))

(defn inject [s placeholder content]
  (let [i (str/index-of s placeholder)]
    (str (subs s 0 i) content (subs s (+ i (count placeholder))))))

(def out
  (-> (fs/slurp "script/index.template.mjs")
      (inject "%CONSTS%" consts)
      (inject "%EXPORTS%" exports)))

(fs/spit "index.mjs" out)
(println "wrote index.mjs:" (count names) "functions wrapped")
