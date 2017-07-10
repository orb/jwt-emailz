(ns emailz.handler
  (:require [buddy.sign.jwt :as jwt]
            [clj-time.core :as time]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [emailz.mail :as mail]
            [emailz.startup]
            [hiccup.core :as h]
            [hiccup.page :as page]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.util.codec :as codec]
            [ring.util.response :as response]))


(defn layout [& body]
  (h/html
   (page/html5
    [:html
     [:head
      [:title "Emailz JWT example"]
      [:meta {:charset "utf-8"}]
      [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
      [:meta {:name "viewpoert" :content "width=device-width, initial-scale=1"}]
      [:link {:rel :stylesheet :href "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"}]
      [:link {:rel :stylesheet :href "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css"}]]
     [:body
      [:div.container body]
      [:script {:src "https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"}]
      [:script {:src "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"}]]])))


(defn five-minutes-from-now []
  (time/plus (time/now)
             (time/minutes 5)))

(defn gen-jwt [email]
  (buddy.sign.jwt/sign {:sub email
                        :exp (five-minutes-from-now)}
                       @emailz.startup/secret))
(defn verify-jwt [jwt]
  (buddy.sign.jwt/unsign jwt
                         @emailz.startup/secret))

(defn home [req]
  (layout
   [:h1 "Home page!"]
   (if-let [email (get-in req [:session :email])]
     (list [:p.lead "Hello there, " (h/h email)]
           [:p [:a {:href "/logout"} "logout"]])
     [:div.row
      [:div.col-sm-offset-4.col-sm-4
       [:form {:method :get :action "/mail"}
        [:h2 "Enter your email to login"]
        [:input.form-control  {:type :email
                               :name "email"
                               :placeholder "Email address"
                               :required ""
                               :autofocus ""}]
        [:button.btn-lg.btn-primary.btn-block {:type :submit} "Sign in"]]]])))


(defn verify [req]
  (if-let [current-email (get-in req [:session :email])]
    (layout
     [:h1 "Already logged in"]
     [:p.lead "Maybe you want to "
      [:a {:href "/logout"} "logout"]
      " or go "
      [:a {:href "/"} "home"]])
    (let [jwt (get-in req [:params :jwt])]
      (try
        (let [jwt-data (verify-jwt jwt)
              email    (:sub jwt-data)]
          (-> (response/response
               (layout
                [:h1 "Hey there, " (h/html email)]
                [:p.lead "Go " [:a {:href "/"} "home"]]))
              (response/content-type "text/html")
              (assoc :session {:email email})))
        (catch Exception e
          (let [ex (ex-data e)]
            (println "ex" e ex)
            (layout
             [:h1 "Invalid..."]
             (cond
               (= :exp (:cause ex))
               [:p.lead "Your login expired. Please be a bit faster next time..."]

               (= :signature (:cause ex))
               [:p.lead "That's not a valid JWT!"]

               :else
               nil)
             [:p.lead "Go " [:a {:href "/"} "home"]])))))))

(defn logout [req]
  (-> (response/redirect "/")
      (assoc :session {})))

(defn relative-url [req path query]
  (format "%s://%s%s%s"
          (name (:scheme req))
          (get-in req [:headers "host"])
          path
          (if query
            (str "?" (codec/form-encode query))
            "")))


(defn send-mail [req]
  (let [email (get-in req [:params :email])
        jwt (gen-jwt email)
        verify-url (relative-url req "/verify" {:jwt jwt})]
    (mail/send-verify email verify-url)
    (layout
     [:h1 "Check your mail!"])))

(defroutes app-routes
  (GET "/" [] home)
  (GET "/mail" [] send-mail)
  (GET "/verify" [] verify)
  (GET "/logout" [] logout)
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes
                 (-> site-defaults
                     (assoc-in [:security :anti-forgery] false))))
