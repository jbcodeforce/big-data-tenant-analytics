import boto3
import csv, json

ENDPOINT_NAME="linear-learner-2022-12-22-23-12-40-646"
payload="24670,6168,7,41,975,1645,0,0,0,0,0,0,0,0,1"

runtime=boto3.client("runtime.sagemaker")
response = runtime.invoke_endpoint(EndpointName=ENDPOINT_NAME,
                                    ContentType='text/csv',
                                    Body=payload)
result = json.loads(response['Body'].read().decode())
prediction = result['predictions'][0]['score']
print(result)
print(prediction)