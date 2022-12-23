#!/usr/bin/env python3
import os

import aws_cdk as cdk

from apigw_lambda_cdk.apigw_lambda_cdk_stack import ApigwLambdaCdkStack


app = cdk.App()
ApigwLambdaCdkStack(app, "ApigwLambdaCdkStack")

app.synth()
