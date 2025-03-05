(ns gorillalabs.collections)

(defn assoc-if
  "Associates a key with a value in a map if the value is not nil."
  ([map key val] (if val
                   (assoc map key val)
                   map))
  ([map key val & kvs]
   (let [ret (assoc-if map key val)]
     (if kvs
       (if (next kvs)
         (recur ret (first kvs) (second kvs) (nnext kvs))
         (throw (IllegalArgumentException.
                  "assoc-if expects even number of arguments after map/vector, found odd number")))
       ret))))

(defn keys-in
  "Returns a sequence of all key paths in a given map using DFS walk."
  [m]
  (letfn [(children [node]
            (let [v (get-in m node)]
              (if (map? v)
                (map (fn [x] (conj node x)) (keys v))
                [])))
          (branch? [node] (-> (children node) seq boolean))]
    (->> (keys m)
         (map vector)
         (mapcat #(tree-seq branch? children %)))))

(defn deep-merge
  "2-arity deep merge, see https://gist.github.com/danielpcox/c70a8aa2c36766200a95?permalink_comment_id=2759497#gistcomment-2759497"
  [a b]
  (if (map? a)
    (merge-with deep-merge a b)
    b))
