#!/usr/bin/env bash
FUNCTIONAL_TEST_PATH="src/test/functional"

run_test() {
  cd $FUNCTIONAL_TEST_PATH/$1 && mvn idris:compile idris:run
  cd -
}

echo "Trying ls from IDRIS2 lib directory to see if it exists in GHA"
cd lib && ls
cd -
run_test dependencies-test
