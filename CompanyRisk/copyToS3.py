import boto3,os,argparse


AWS_ACCESS_KEY_ID=os.environ.get("AWS_ACCESS_KEY_ID")
AWS_SECRET_ACCESS_KEY=os.environ.get("AWS_SECRET_ACCESS_KEY")


parser = argparse.ArgumentParser()
parser.add_argument("region", help="Must specify the AWS region for S3")
parser.add_argument("bucket", help="Must specify a S3 bucket name to upload files to")
parser.add_argument("filename", help="Must specify a file name to upload")
args = parser.parse_args()

if args.region is None:
    s3 = boto3.resource('s3')
else:
    s3 = boto3.resource('s3',region_name=args.region)
    location = {'LocationConstraint': args.region}

from botocore.client import ClientError

try:
    s3.meta.client.head_bucket(Bucket=args.bucket)
except ClientError:
    s3.create_bucket(Bucket=args.bucket)

bucket = s3.Bucket(args.bucket)
    
object_name = os.path.basename(args.filename)

bucket.upload_file(args.filename, object_name)


print("Done !")