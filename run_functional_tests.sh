#!/usr/bin/env bash
FUNCTIONAL_TEST_PATH="src/test/functional"

run_test() {
  cd $FUNCTIONAL_TEST_PATH/$1 && mvn idris:compile idris:run
  cd -
}

mvn install
run_test dependencies-test