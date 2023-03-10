import os
import boto3
import json

STREAM_NAME = "companies"
my_session = boto3.session.Session()
my_region = my_session.region_name
kinesis_client = boto3.client('kinesis',region_name=my_region)

def sendCompanyJson():
    company={"companyID" : "comp_4",
            "industry": "retail",
            "revenu": 100000,
            "employees": 4635,
            "job30": 10,
            "job90":100,
            "monthlyFee": 400.00,
            "totalFee": 1200.00
    }
    companyAsString =json.dumps(company)
    print(companyAsString)
    kinesis_client.put_record(
                StreamName=STREAM_NAME,
                Data=companyAsString,
                PartitionKey="partitionkey")

def sendCompanyCSV():
    f = open('./data/companies.csv', 'r')

    for line in f:
        print(line)
        kinesis_client.put_record(
                StreamName=STREAM_NAME,
                Data=line,
                PartitionKey="partitionkey")

sendCompanyJson()