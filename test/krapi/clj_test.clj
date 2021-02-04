(ns krapi.clj-test
  (:require [clojure.test :refer :all]
            [krapi.clj :refer :all]))

(deftest b64decode-test
  (testing "Decode base64 to string"
    (is (= "hi" (String. (b64decode "aGk=") "UTF-8"))))
  (testing "Encode base64 to string"
    (is (= "Ynll" (b64encode (.getBytes "bye" "UTF-8"))))))
(deftest get-nonce-test
  (testing "Get a nonce"
    (let [old-nonce (get-nonce)]
      (is (int? old-nonce))
      (is (not= (get-nonce) (get-nonce)))
      (is (< old-nonce (get-nonce))))))
