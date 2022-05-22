#!/bin/sh
FUNCTIONAL_TEST_PATH="src/test/functional"

run_test() {
  cd $FUNCTIONAL_TEST_PATH/$1 && mvn idris:compile idris:run
  cd -
}

echo "IDRIS2_PREFIX: $IDRIS2_PREFIX"
run_test dependencies-test
