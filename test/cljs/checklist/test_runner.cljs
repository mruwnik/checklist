(ns checklist.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [checklist.core-test]
   [checklist.common-test]))

(enable-console-print!)

(doo-tests 'checklist.core-test
           'checklist.common-test)
