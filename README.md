# krapi.clj

A Clojure library designed to interface with Kraken.com REST API both public and private.

## Usage

The public API operations should be available to all, however, private API operations require a valid api-key and api-secret available from your Kraken account.

Kraken makes use of rate limiting and complex rules about how this works. Familiarise yourself with their clear and concise documentation. The only issue is the error codes can be confusing and failed queries still are subject to rate limiting. You can easily be banned from the API if you abuse them.

## Dependencies

* clj-http "3.12.0"
* cheshire "5.10.0"
* pandect "1.0.1"

### Examples

```
(defn get-system-status 
  "Get Kraken system status"
  []  (query-public "SystemStatus"))
(defn get-time 
  "Get Kraken server time"
  []  (query-public "Time"))
```

To get a field from within the result you can use `get`.

```
(defn get-server-unix-time 
  "Get Kraken server unix timestamp"
  []  (-> (get-time)
          (get "result")
          (get "unixtime")))
```

Some queries can take parameters.

```
(defn get-assets 
  "Get information about assets on Kraken.
  You can provide a vector of `assets` to filter result"
  ([] (query-public "Assets" {}))
  ([assets]
   (query-public "Assets" {:asset assets})))
(defn get-ticker
  "Get information about assets on Kraken.
  You can filter the results with a vector in `pair`"
  ([]
   (query-public "Ticker" {}))
  ([pair]
   (query-public "Ticker" {:pair pair})))
```

### Parameters
* `api-url` default `https://api.kraken.com/`
* `api-version` default `0`
* `api-key` needed for private methods generate this in your Kraken account. Do not share. Provide it as is.
* `api-secret` needed for private methods generate this in your Kraken account. Do not share it. Provide it as is (do not base64 decode it).

## License

Copyright © 2021 Ali Raheem

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
