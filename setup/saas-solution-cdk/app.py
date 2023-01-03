#!/usr/bin/env python3
import os

import aws_cdk as cdk

from saas_solution_cdk.saas_solution_cdk_stack import SaasSolutionCdkStack
from apigw_lambda_cdk.apigw_lambda_cdk_stack import ApigwLambdaCdkStack

app = cdk.App()
#SaasSolutionCdkStack(app, "SaasSolutionCdkStack",
#    env=cdk.Environment(account=os.getenv('CDK_DEFAULT_ACCOUNT'), region=os.getenv('CDK_DEFAULT_REGION'))
#    )

ApigwLambdaCdkStack(app, "ApigwLambdaCdkStack",
    env=cdk.Environment(account=os.getenv('CDK_DEFAULT_ACCOUNT'), region=os.getenv('CDK_DEFAULT_REGION'))
    )

app.synth()
