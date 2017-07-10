(ns emailz.startup)

(def secret (atom "not-a-real-secret"))

(defn init []
  (println "Emailz starting...")
  (reset! secret (str (java.util.UUID/randomUUID)))
  (println (format "JWT secret is %s. Don't tell anyone" @secret)))



