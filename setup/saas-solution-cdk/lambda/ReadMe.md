# A lambda function to facade SageMaker

The goal of this lambda is to call a scoring model deployed in SageMaker runtime by doing data transformation from json to csv payload and reverse. The approach is to implement an anti-corruption layer. The Lambda will be integrated with an API Gateway.

The deployment can be fully automated with CDK. 

