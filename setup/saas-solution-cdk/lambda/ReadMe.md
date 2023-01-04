# A lambda function to facade SageMaker

The goal of this lambda is to call a scoring model deployed in SageMaker runtime by doing data transformation from json to csv payload and reverse. The approach is to implement an anti-corruption layer. The Lambda will be integrated with an API Gateway.

## Deploy with CDK

Go under the `setup/saas-solution-cdk` and do:

```sh
cdk deploy
```


## Deploy with dependencies

When using XRay for tracing and other serializer like jsonpickle, we need to package the dependencies with the lambda code and push it as Zip.

* Package the dependencies

```sh
mkdir package
pip install --target ./package -r requirements.txt 
cd package
zip -r ../lambda-layer.zip .
```

* Add Lambda code to zip

```sh
zip lambda-layer.zip lambda-handler.py
```

* Update Lambda function with new code:

```
aws lambda update-function-code --function-name  ApigwLambdaCdkStack-SageMakerMapperLambda2EFF1AC9-bERmXFWzvWSC --zip-file fileb://lambda-layer.zip
```