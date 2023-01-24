#!/bin/bash
REPO=jbcodeforce/demo-saas-tenant-mgr
if [[ $# -eq 1 ]]
then
  REPO=$1
fi

aws ecr create-repository --repository-name $REPO \
--image-tag-mutability MUTABLE --output json --region us-west-2 \
--image-scanning-configuration scanOnPush=false