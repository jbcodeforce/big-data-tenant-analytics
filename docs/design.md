# Architecture and Design

## Component view

As presented in the introduction we have the following component view in scope for this demonstration:

![](./diagrams/solution-comp-view.drawio.png)


## EKS cluster creation and solution deployment

### EKS Cluster creation with CDK

To use an infrastructure as code we use CDK to create a EKS cluster. The AWS CDK revolves around a fundamental building block called a construct. These constructs have three abstraction levels:

L1 – A one-to-one mapping to AWS CloudFormation
L2 – An intent-based API
L3 – A high-level pattern


## [Kinesis Data Streams](https://aws.amazon.com/kinesis/data-analytics/)

### Why 

This is a managed service for pub/sub streaming data. It is a distributed data stream into Shards for parallel processing. Producer sends message with `Partition Key` and a throughput of 1 Mb/s or 1000 msg /s per Shard. A sequence number is added to the message to note where the message is in the Shard. 

* Retention from 1 to 365 days.
* Capable to replay the messages.
* Immutable records, not deleted by applications.
* Message in a shard, can share partition key, and keep ordering.
* Producer can use SDK, or Kinesis Producer Library (KPL) or being a Kinesis agent.
* Consumer may use SDK and Kinesis Client Library (KCL), or being one of the managed services like: Lambda, Kinesis Data Firehose, Kinesis Data Analytics.
* For consuming side, each Shard gets 2MB/s out.
* It uses enhanced fan-out if we have multiple consumers retrieving data from a stream in parallel. This throughput automatically scales with the number of shards in a stream.
* Pricing is per Shard provisioned per hour.
* The capacity limits of a Kinesis data stream are defined by the number of shards within the data stream. The limits can be exceeded by either data throughput or the number of reading data calls. Each shard allows for 1 MB/s incoming data and 2 MB/s outgoing data. You should increase the number of shards within your data stream to provide enough capacity.


### Deployment

The CDK app under `cdk/kinesis` folder defines the following components:


And the steps to start them are:

* Start the CDK app to create CloudFormation template and run it

```sh
cd cdk/kinesis
cdk deploy
```

* Verify the stream is created

```sh
aws kinesis list-streams
aws kinesis describe-stream-summary --stream-name bg-jobs
{
    "StreamDescriptionSummary": {
        "StreamName": "bg-jobs",
        "StreamARN": "arn:aws:kinesis:us-west-2:403993201276:stream/bg-jobs",
        "StreamStatus": "ACTIVE",
        "StreamModeDetails": {
            "StreamMode": "PROVISIONED"
        },
        "RetentionPeriodHours": 24,
        "StreamCreationTimestamp": "2022-12-20T21:11:04-08:00",
        "EnhancedMonitoring": [
            {
                "ShardLevelMetrics": []
            }
        ],
        "EncryptionType": "KMS",
        "KeyId": "alias/aws/kinesis",
        "OpenShardCount": 1,
        "ConsumerCount": 0
    }
}
```

* Put records

* Validate Records, even if data is encrypted by default:

```sh
SHARD_ITERATOR=$(aws kinesis get-shard-iterator --shard-id shardId-000000000000 --shard-iterator-type TRIM_HORIZON --stream-name bg-jobs --query 'ShardIterator')

aws kinesis get-records --shard-iterator $SHARD_ITERATOR
```

## [Kinesis Data Analytics](https://aws.amazon.com/kinesis/data-analytics/)

This is a managed service to transform and analyze streaming data in real time using Apache Flink, an open-source framework and engine for processing data streams. It can consume records from different source, and in this demonstration we use Kinesis Data Streams.

![](https://d1.awsstatic.com/architecture-diagrams/Product-Page-Diagram_Amazon-Kinesis-Data-Analytics_HIW.82e3aa53a5c87db03c766218b3d51f1a110c60eb.png)

The underlying architecture consists of a **Job Manager** and n **Task Managers**. 

The **JobManager** controls the execution of a single application. It receives an application for execution and builds a Task Execution Graph from the defined Job Graph. It manages job submission and the job lifecycle then allocates work to Task Managers
The **Resource Manager** manages Task Slots and leverages underlying orchestrator, like Kubernetes or Yarn.
A **Task slot** is the unit of work executed on CPU.
The **Task Managers** execute the actual stream processing logic. There are multiple task managers running in a cluster. The number of slots limits the number of tasks a TaskManager can execute. After it has been started, a TaskManager registers its slots to the ResourceManager

![](./diagrams/flink-arch.drawio.svg)


### When to choose what

As Apache Flink is an open-source project, it is possible to deploy it in a Kubernetes cluster, using Flink operator. This will bring you with the most flexible solution as you can select the underlying EC2 instances needed, to optimize your cost. Also you will have fine-grained control over cluster settings, debugging tools and monitoring.

While Kinesis Data Analytics helps you to focus on the application logic, which is not simple programming experience, as stateful processing is challenginf, there is no management of infrastructure, monitoring, auto scaling and high availability integrated in the service.

In addition to the AWS integrations, the Kinesis Data Analytics libraries include more than 10 Apache Flink connectors and the ability to build custom integrations. 

### Implementation details


Joins between company and job streams on the company ID and add the number of jobs run (from job event) to the company current jobs count.

    * Company event is a csv with: company_id, industry, revenu, employees, job30, job90, monthlyFee, totalFee
    * Job is: company_id, userid , #job_submitted
    * Out come is : company_id, industry, revenu, employees, job30 + #job_submitted, job90 + #job_submitted, monthlyFee, totalFee


kinesis-analytics-JobProcessing-us-west-2


## QuickSight Integration Design

The Dashboard is supported by Amazon QuickSight, with the datasets defined for customers and jobs.

![](./diagrams/qs-arch.drawio.png)

The data sets are defined from the S3 bucket / files for companies.csv and job.csv that were created by the ingestion layer supported by Kinesis Data Analytics.

A way to deploy [quicksight with cloudFormation](https://devops.learnquicksight.online/quicksight-via-cloudformation.html)