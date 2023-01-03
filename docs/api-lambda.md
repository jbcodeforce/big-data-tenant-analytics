# Anti-corruption layer with Lambda function

As illustrated by the architecture diagram, SageMaker is not directly accessed from the real time analytic component but via an API Gateway and a lambda function:

![](./diagrams/solution-comp-view.drawio.png)

 The approach is to implement an anti-corruption layer where the data model of SageMaker is not corrupting the Tenant domain.

## Lambda

The goal of the lambda function is to call a scoring model deployed in SageMaker runtime with csv payload, while receiving triggering Event in json Company model. So it does data transformation from json to csv payload and reverse.

The code is defined in [lambda-handler.py](https://github.com/jbcodeforce/big-data-tenant-analytics/blob/main/setup/saas-solution-cdk/lambda/lambda-handler.py).

The deployment can be fully automated with CDK.

For this lambda to be able to access SageMaker it needs a IAM Role with SageMaker invokeEndpoint permission, and generate logs (`AWSLambdaBasicExecutionRole`).

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": [
                "sagemaker:InvokeEndpoint"
            ],
            "Resource": "*",
            "Effect": "Allow"
        }
    ]
}
```