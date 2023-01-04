import os
import io
import boto3
import csv, logging, json

logger = logging.getLogger()
logger.setLevel(logging.INFO)

# grab environment variables
ENDPOINT_NAME = os.environ['ENDPOINT_NAME']
runtime= boto3.client('runtime.sagemaker')

def lambda_handler(event, context):
    try:
        logger.info('## ENVIRONMENT VARIABLES\r' + json.dumps(dict(**os.environ)))
        eventJsonStr=json.dumps(event)
        logger.info('## EVENT\r' + eventJsonStr)
        logger.info('## CONTEXT\r' + str(context))
        industryMapping={   "consulting": "1,0,0,0,0,0,0,0",
                            "retail"   : "0,1,0,0,0,0,0,0", 
                            "service"  : "0,0,1,0,0,0,0,0",
                            "health"   : "0,0,0,1,0,0,0,0", 
                            "finance"  : "0,0,0,0,1,0,0,0", 
                            "gov"      : "0,0,0,0,0,1,0,0",
                            "travel"   : "0,0,0,0,0,0,1,0",
                            "energy"   : "0,0,0,0,0,0,0,1",
                          }
        fullRequest = json.loads(eventJsonStr)
        companyJsonStr = fullRequest['body']
        logger.info('payload:' + companyJsonStr)
        companyJson = json.loads(companyJsonStr)
        payload= str(companyJson['revenu']) \
            + "," + str(companyJson['employee']) \
            + "," + str(companyJson['job30']) \
            + "," + str(companyJson['job90']) \
            + "," + str(companyJson['monthlyFee']) \
            + "," + str(companyJson['totalFee']) \
            + "," + industryMapping[companyJson['industry']]
            
        response = runtime.invoke_endpoint(EndpointName=ENDPOINT_NAME,
                                        ContentType='text/csv',
                                        Body=payload)
        result = json.loads(response['Body'].read().decode())
        prediction = result['predictions'][0]
    
    except Exception as e:
        # Send some context about this error to Lambda Logs
        print(e)
        raise e
    
    return {
        'statusCode': 200,
        'body': '{ "payload":' + payload + ',"result":' + str(prediction) +'}'
    }