#!/usr/bin/env bash
FUNCTIONAL_TEST_PATH="src/test/functional"

errs=0
run_test() {
  cd $FUNCTIONAL_TEST_PATH/$1 && mvn idris:compile idris:run || ((errs++))
  cd -
}

# Install the maven plugin first before running tests
mvn install

# If there is a test failure, run every functional test before exiting
run_test dependencies-test 

exit $errs
