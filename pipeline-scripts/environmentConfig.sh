#!/bin/bash

# The @DynamoDBTable annotation expects the table name to be an inline constant. This makes setting the table name
# at runtime problematic. This script sets the table name to the dev environment's dynamodb table when running
# a pipeline on a non-master branch.

ENVIRONMENT=$1
if [ "$ENVIRONMENT" = "DEV" ] ; then
  echo "Making code changes for dev environment"
  sed -i 's/TriviaRoyale/TriviaRoyaleDevelopment/' src/main/groovy/com/triviaroyale/data/util/DynamoDBConstants.groovy
  sed -i "s/LEADERBOARD_KEY_PREFIX = .*/LEADERBOARD_KEY_PREFIX = 'LB_DEV_'/" src/main/groovy/com/triviaroyale/service/LeaderboardService.groovy
fi

cp src/main/resources/$ENVIRONMENT.logging.properties src/main/resources/logging.properties
rm -f src/main/resources/*.logging.properties