#!/bin/bash

# The @DynamoDBTable annotation expects the table name to be an inline constant. This makes setting the table name
# at runtime problematic. This script sets the table name to the dev environment's dynamodb table when running
# a pipeline on a non-master branch.

ENVIRONMENT=$1
if [ "$ENVIRONMENT" = "DEV" ] ; then
  echo "Changing DynamoDB Table to dev table"
  sed -i 's/TriviaRoyale/TriviaRoyaleDevelopment/' src/main/groovy/com/triviaroyale/data/util/DynamoDBConstants.groovy
fi
