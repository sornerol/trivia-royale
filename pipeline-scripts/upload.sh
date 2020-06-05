#!/bin/bash

source $(dirname "$0")/variables.sh

aws s3 rm s3://$S3_DEPLOY_BUCKET/*.zip
aws s3 cp $DEPLOYMENT_PACKAGE_PATH/$PACKAGE_NAME s3://$S3_DEPLOY_BUCKET/$PACKAGE_NAME
