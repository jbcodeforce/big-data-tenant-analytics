import boto3,json

STREAM_NAME = "bg-jobs"
my_session = boto3.session.Session()
my_region = my_session.region_name
kinesis_client.get_record(
            StreamName=stream_name,
            Data=json.dumps(data),
            PartitionKey="partitionkey")