#!/bin/bash

source $(dirname "$0")/variables.sh

deployTime=$(date +%m%d%Y_%H%M%S)
aws lambda update-function-code --function-name $LAMBDA_FUNCTION_NAME \
  --s3-bucket $S3_DEPLOY_BUCKET \
  --s3-key $PACKAGE_NAME

aws s3 mv s3://$S3_DEPLOY_BUCKET/$PACKAGE_NAME s3://$S3_DEPLOY_BUCKET/deployed/$deployTime_$PACKAGE_NAME
