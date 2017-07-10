(ns emailz.mail
  (:require [clojurewerkz.mailer.core :as mailer]))

(defn send-verify [email link]
  (mailer/deliver-email
   {:from "orb@nostacktrace.com"
    :to ["orb@nostacktrace.com"]
    :subject "Please verify your account"}
   "verify.mustache"
   {:link link}))
