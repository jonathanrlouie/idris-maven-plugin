#!/bin/sh
FUNCTIONAL_TEST_PATH="src/test/functional"

run_test() {
  cd $FUNCTIONAL_TEST_PATH/$1 && mvn idris:run
  cd -
}

run_test dependencies-test
