from aws_cdk import (
    # Duration,
    Stack,
    aws_lambda,
    aws_iam as iam,
    aws_apigateway as apigw
)
from constructs import Construct

class ApigwLambdaCdkStack(Stack):

    def __init__(self, scope: Construct, construct_id: str, **kwargs) -> None:
        super().__init__(scope, construct_id, **kwargs)

        invokeSageMakerPolicy = iam.Policy(self,"InvokeSageMaker",
            statements=[iam.PolicyStatement(
                actions=["sagemaker:InvokeEndpoint"],
                effect=iam.Effect.ALLOW,
                resources=["*"]
            )])
        sm_role= iam.Role(self,"SageMakerClientRole",
                 assumed_by=iam.ServicePrincipal("lambda.amazonaws.com"))
        sm_role.add_managed_policy(iam.ManagedPolicy.from_aws_managed_policy_name("service-role/AWSLambdaBasicExecutionRole"))
        sm_role.add_managed_policy(iam.ManagedPolicy.from_aws_managed_policy_name("AWSXRayDaemonWriteAccess"))
        #sm_role.add_managed_policy(iam.ManagedPolicy.from_aws_managed_policy_name("service-role/AWSLambdaVPCAccessExecutionRole"))
        sm_role.attach_inline_policy(invokeSageMakerPolicy)

        base_lambda = aws_lambda.Function(self, 'SageMakerMapperLambda',
                                       handler='lambda-handler.lambda_handler',
                                       description= "Lambda function to Invoke Sagemaker Endpoint",
                                       runtime=aws_lambda.Runtime.PYTHON_3_8,
                                       code=aws_lambda.Code.from_asset('lambda'),
                                       tracing=aws_lambda.Tracing.ACTIVE,
                                       environment= { "ENDPOINT_NAME": "linear-learner-2022-12-28-02-41-34-883"},
                                       role=sm_role)

        base_api = apigw.LambdaRestApi(self, 'ApiGateway',
                                  rest_api_name='api-Churn-SM',
                                  handler=base_lambda,
                                  description="API Gateway to call Lambda",
                                  proxy=False)
        
        sagemakerProxy = base_api.root.add_resource('assessChurn')
        sagemakerProxy.add_method("POST")
        
