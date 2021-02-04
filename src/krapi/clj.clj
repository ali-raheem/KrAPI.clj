(ns krapi.clj
  (:require [clj-http.client :as client]
            [cheshire.core :refer :all]
            [pandect.algo.sha256 :as sha256] 
            [pandect.algo.sha512 :as sha512]))

(def api-version (atom 0))
(def api-url (atom "https://api.kraken.com/"))
(def api-key (atom ""))
(def api-secret (atom ""))
(swap! api-url #(str % @api-version))

(defn init-private
  "Initialise the private API query functions.
  You must provide a valid `api-key-` and `api-secret-` to make use of `query-private`."
  [api-key- api-secret-]
  (reset! api-key api-key-)
  (reset! api-secret api-secret-))
(defn init
  "Initialise the API connections, this is optional as defaults exist:

  * `api-url-` is set to \"https://api.kraken.com/\"
  * `api-version-` is set to \"0\" "
  [api-url- api-version-]
  (reset! api-url api-url-)
  (reset! api-version api-version-))

(defn b64decode
  "Decode Base64 `encoded` data returning a byte-array."
  [encoded]
  (.decode (java.util.Base64/getDecoder)  encoded))
(defn b64encode
  "Base64 encode `data` returning a String."
  [data]
  (.encodeToString (java.util.Base64/getEncoder) data))
(def nonce-counter- (atom 0))
(defn get-nonce
  "Return a integer to be used as a nonce.
  Strictly increasing value based on the local time in milliseconds."
  []
  (+ (swap! nonce-counter- #(mod (inc %) 1000)) 
     (* 1000 (System/currentTimeMillis))))

(defn concat-bytes
  "Takes byte-array `a` and `b` and returns a byte-array of `b` appended to `a`."
  [a b]
  (let [a_len (alength a)
        b_len (alength b)
        out (byte-array (+ a_len b_len))]
    (System/arraycopy a 0 out 0 a_len)
    (System/arraycopy b 0 out a_len b_len)
    out))

(defn parse-json-response
  "Parse the body of a json `res`ponse from a api query"
  [res]
  (parse-string (:body res)))

(defn query-private
  "Query a private Kraken API operation.
  Operation is requested in `path`, you can provide a map of the arguments in `params`.
  The URI will be /api-version/private/`path`, case matters. e.g. \"/0/private/DepositAddresses\"
  `params` examples could be {:asset [\"XMR\" \"BTC\"]}
  A `nonce` can also be provided or generated locally. It is a 64bit unsigned integer. You should not mix `nonce` generation methods."
  ([path] (query-private path {} (get-nonce)))
  ([path params]
   (query-private path params (get-nonce)))
  ([path params nonce]
   (let [uri (.getBytes  (str "/" @api-version "/private/" path) "UTF-8")
         params (merge {"nonce" nonce} params)
         query_string (client/generate-query-string params)
         hash (sha256/sha256-bytes (str nonce query_string))
         uri_hash (concat-bytes uri hash)
         mac (sha512/sha512-hmac-bytes uri_hash (b64decode @api-secret))]
     (parse-json-response 
      (client/post
       (str @api-url "/private/" path) 
       {:accept :json
        :headers {"API-Key" @api-key
                  "API-Sign" (b64encode mac)}
        :form-params params})))))

(defn query-public
  "Query a public Kraken API operation.
  Operation is requested in `path`, you can provide a map of the arguments in `params`.
  The URI will be /api-version/public/`path`, case matters. e.g. \"/0/public/Time\".
  `params` examples could be {:asset [\"XMR\" \"BTC\"]}
  Provide a operation in `path` and a map of arguments in `data`"
  ([path] (query-public path ""))
  ([path params]
   (parse-json-response
    (client/get (str @api-url "/public/" path)
                {:multi-param-style :comma-separated
                 :accept :json
                 :query-params params}))))


