(defn func
  [a b]
  (and
    (> 56
      (+
        (+ a b)
        (*
          <selection>(+ a b)</selection>
          (/ a
            (+ a b)))
        (+ a b)))))