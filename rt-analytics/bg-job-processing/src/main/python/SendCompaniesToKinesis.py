import os
import boto3

STREAM_NAME = "companies"
my_session = boto3.session.Session()
my_region = my_session.region_name
kinesis_client = boto3.client('kinesis',region_name=my_region)

f = open('./data/companies.csv', 'r')

for line in f:
    print(line)
    kinesis_client.put_record(
            StreamName=STREAM_NAME,
            Data=line,
            PartitionKey="partitionkey")