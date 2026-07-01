;; Regenerates index.mjs (the JS API) from script/index.template.mjs by importing
;; the compiled module at runtime, reading its exports, and demunging the squint
;; names back to their Clojure names. Run via nbb: `bb gen-index` (builds lib first).

(require '["node:fs" :as node-fs]
         '["../lib/babashka/fs.mjs" :as fs]
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

(def rows
  (for [n names
        :let [c (camel (demunge n))]
        :when (not= n c)]
    [n c]))

(def alias-rows (filter (fn [[_ c]] (reserved c)) rows))
(def const-rows (remove (fn [[_ c]] (reserved c)) rows))

(def imports
  (str/join ",\n" (map #(str "  " %) names)))

(def consts
  (str/join "\n"
            (map (fn [[n c]]
                   (if (value? n)
                     (str "const " c " = " n ";")
                     (str "const " c " = jsFriendly(" n ");")))
                 const-rows)))

(def exports
  (str "  // squint names\n"
       (str/join ",\n" (map #(str "  " %) names))
       ",\n  // friendly camelCase aliases\n"
       (str/join ",\n" (map (fn [[_ c]] (str "  " c)) const-rows))
       (when (seq alias-rows)
         (str ",\n  // reserved-word names (no option translation, none needed)\n"
              (str/join ",\n" (map (fn [[n c]] (str "  " n " as " c)) alias-rows))))))

(defn inject [s placeholder content]
  (let [i (str/index-of s placeholder)]
    (str (subs s 0 i) content (subs s (+ i (count placeholder))))))

(def out
  (-> (.readFileSync node-fs "script/index.template.mjs" "utf8")
      (inject "%IMPORTS%" imports)
      (inject "%CONSTS%" consts)
      (inject "%EXPORTS%" exports)))

(.writeFileSync node-fs "index.mjs" out)
(println "wrote index.mjs:" (count names) "exports," (count rows) "aliases")
