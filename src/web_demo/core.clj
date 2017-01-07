(ns web-demo.core
  (:require
    [calfpath.core :as r]
    [couchbase-clj.client :as c]
    [clojure.tools.logging :as log]
    [hiccup.core :as h]
    [ring.adapter.jetty :refer [run-jetty]]
    [ring.middleware.session :refer [wrap-session]]
    [ring.middleware.reload :refer [wrap-reload]])
  (:gen-class))

(def data (atom {"alpha" "1"
           "beta"  "2"
           "omega" "3"
           "delta" "4"
           "echo" "5"}))

(defn html-response
  [handler]
  (fn [request]
      (let [resp (handler request)]
        (assoc-in resp [:headers] "Content-Type" "text/html"))))

(defn handle-visitor
  [request]
  (let [s (:session request)
        visits  (or (:count s) 0)]
  {:status 200
   :body (str "Hai there this is your " visits "th visit")
   :session {:count (inc visits)}}))

(defn handle-visitor-reset
  [request]
  {:status 200
   :body (str "So long and thanks for all the fish")
   :session nil})

(defn random-pair-generator
  [request]
  (let [k (str (java.util.UUID/randomUUID))
        v (str (java.util.UUID/randomUUID))]
    (log/info "got a request to gen pair")
    (swap! data (fn [d] (assoc d k v)))
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (str "key is " k "<br/>"
                "value is  " v "<br/>")}))

(defn get-secret
  [request id]
  (let [v (get @data id)]
    (if (nil? v)
      {:status 404 :body "key not found"}
      {:status 200 :body (str "secret is " v)})))

(defn handle-home
  [request]
  {:status 200
   :body (h/html [:p "This is a paragraph"])})

(defn route-handler
  ;;[c]
  ;; (fn [request]
  [request]
  (r/->uri request
      "/" [] (r/->get request (handle-home request))
      "/visit/"  []  (r/->get request (handle-visitor request))
      "/visit/reset/" [] (r/->get request (handle-visitor-reset request))
      "/pair/" [] (r/->get request (random-pair-generator request))
      "/secret/:id/" [id] (r/->get request (get-secret request id))))

(def reloadable-app (wrap-reload (wrap-session route-handler)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; (let [c (create-cache)]
  ;; (run-jetty (reloadable-app cache))
  (run-jetty reloadable-app {:port 4500}))
