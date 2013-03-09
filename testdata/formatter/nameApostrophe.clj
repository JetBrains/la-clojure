(defn dotify-graph' [{:keys [nodes edges]}]
  (str
    "graph{"
    (dotify-nodes nodes)
    (dotify-edges' edges)
    "}"))