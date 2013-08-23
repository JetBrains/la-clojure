(defn pascal-trapezoid
  [coll]
  (letfn [(next-row [c] (map
                          <selection> (partial reduce +) </selection>
                                      (partition
                                        2
                                        1
                                        (cons 0
                                          (conj
                                            (vec c)
                                            0)))))]
    (cons
      (vec coll)
      (lazy-seq (pascal-trapezoid (next-row coll))))))