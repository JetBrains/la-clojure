(defn test
  [a t]
  (let [a 3
        b (+ 4 a)]
    (filter
      odd?
      (range<caret> 10))))