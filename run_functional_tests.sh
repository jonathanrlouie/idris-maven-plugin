#!/bin/sh
FUNCTIONAL_TEST_PATH="src/test/functional"

run_test() {
  cd $FUNCTIONAL_TEST_PATH/$1 && mvn idris:compile idris:run
  cd -
}

echo "Trying pwd from IDRIS2 lib directory to see if it exists in GHA"
cd lib && pwd
cd -
run_test dependencies-test
