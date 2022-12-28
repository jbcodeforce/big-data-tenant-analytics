import boto3
import csv, json

ENDPOINT_NAME="linear-learner-2022-12-28-02-41-34-883"
payload="24670,6168,7,41,975,1645,0,0,0,0,0,1,0,0"
payload2="99420,49710,7,97,424,3039,1,0,0,0,0,0,0,0"

runtime=boto3.client("runtime.sagemaker")
response = runtime.invoke_endpoint(EndpointName=ENDPOINT_NAME,
                                    ContentType='text/csv',
                                    Body=payload)
result = json.loads(response['Body'].read().decode())
prediction = result['predictions'][0]['score']
print(result)
print(prediction)