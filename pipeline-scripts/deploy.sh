#!/bin/bash

set -x

MAX_PACKAGE_RETENTION=3

ENVIRONMENT=$1
source $(dirname "$0")/variables-$ENVIRONMENT.sh

deployTime=$(date +%m%d%Y_%H%M%S)
aws lambda update-function-code --function-name trivia-royale \
  --s3-bucket $S3_DEPLOY_BUCKET \
  --s3-key $PACKAGE_NAME

aws s3 mv s3://$S3_DEPLOY_BUCKET/$PACKAGE_NAME s3://$S3_DEPLOY_BUCKET/deployed/"$deployTime"-$PACKAGE_NAME

DEPLOYED_PACKAGES=($(aws s3 ls s3://$S3_DEPLOY_BUCKET/deployed/ |grep $ENVIRONMENT |awk '{print $NF}'))
PACKAGE_COUNT=$(echo ${#DEPLOYED_PACKAGES[@]})

if [ "$PACKAGE_COUNT" -gt "$MAX_PACKAGE_RETENTION" ] ; then
  EXCESS_PACKAGES=$(expr $PACKAGE_COUNT - $MAX_PACKAGE_RETENTION)
  i=0
  while [ $i -lt $EXCESS_PACKAGES ] ; do
    echo "Removing s3://$S3_DEPLOY_BUCKET/deployed/${DEPLOYED_PACKAGES[$i]}"
    aws s3 rm s3://$S3_DEPLOY_BUCKET/deployed/${DEPLOYED_PACKAGES[$i]}
    ((i++))
  done
fi

exit 0
