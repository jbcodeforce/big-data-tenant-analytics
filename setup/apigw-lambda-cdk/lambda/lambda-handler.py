import os
import io
import boto3
import json
import csv

# grab environment variables
ENDPOINT_NAME = os.environ['ENDPOINT_NAME']
runtime= boto3.client('runtime.sagemaker')

def lambda_handler(event, context):
    try:
        data = json.loads(json.dumps(event))
        payload = data['data']
        response = runtime.invoke_endpoint(EndpointName=ENDPOINT_NAME,
                                        ContentType='text/csv',
                                        Body=payload)
        result = json.loads(response['Body'].read().decode())
        prediction = result['predictions'][0]['score']
    
    except Exception as e:
        # Send some context about this error to Lambda Logs
        print(e)
        raise e
    
    return {
        'statusCode': 200,
        'body': '{ "payload":' + payload + ',"prediction":' + str(prediction) +'}'
    }