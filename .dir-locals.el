((nil
  (eval . (cider-register-cljs-repl-type 'chestnut "(do (user/go) (user/cljs-repl))"))
  (cider-refresh-before-fn . "reloaded.repl/suspend")
  (cider-refresh-after-fn  . "reloaded.repl/resume")
  (cider-default-cljs-repl . chestnut)))
