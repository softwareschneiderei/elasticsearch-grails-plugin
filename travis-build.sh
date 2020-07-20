#!/bin/bash

set -e

rm -rf *.zip

./gradlew clean check assemble

filename=$(find build/libs -name "*.jar" | head -1)
filename=$(basename "$filename")

echo "branch: $TRAVIS_BRANCH"
echo "pullrequest: $TRAVIS_PULL_REQUEST"
echo "travis tag: $TRAVIS_TAG"

EXIT_STATUS=0
if [[ $TRAVIS_TAG =~ ^v[[:digit:]] && $TRAVIS_REPO_SLUG == puneetbehl/elasticsearch-grails-plugin && $TRAVIS_PULL_REQUEST == 'false' && $TRAVIS_BRANCH =~ ^master|[234]\..\.x$ ]]; then

  echo "Publishing archives"
  echo "Running Gradle publish for branch $TRAVIS_BRANCH"

  export GRADLE_OPTS="-Xmx1500m -Dfile.encoding=UTF-8"

  ./gradlew --stop
  ./gradlew --no-daemon publish bintrayUpload --stacktrace || EXIT_STATUS=$?

elif [[ $TRAVIS_PULL_REQUEST == 'false' && $TRAVIS_BRANCH =~ ^master|[234]\..\.x$ ]]; then
    echo "Publishing archives for branch $TRAVIS_BRANCH"
    ./gradlew publish --no-daemon --stacktrace || EXIT_STATUS=$?
else
  echo "Skip publishing archives"
fi

./publish-docs.sh

exit $EXIT_STATUS
