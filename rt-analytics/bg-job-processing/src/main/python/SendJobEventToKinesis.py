import os
import boto3

STREAM_NAME = "bigdatajobs"
my_session = boto3.session.Session()
my_region = my_session.region_name
kinesis_client = boto3.client('kinesis',region_name=my_region)

jobEvent='{"companyID": "comp_4", "userID": "user_1", "nbJobs" : 1"}'

kinesis_client.put_record(
            StreamName=STREAM_NAME,
            Data=jobEvent,
            PartitionKey="partitionkey")