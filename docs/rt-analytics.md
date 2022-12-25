# Real-time analytics with Kinesis Data Analytics

The goal of this component is to compute stateful analytics, do data transformation, from the data coming in streams. The current implementation illustrates remote async calls to SageMaker (via API Gateway) and persistence to S3.

![](./diagrams/qs-arch.drawio.png)

**Figure 1**

## Kinesis Data Streams

There is nothing special in this demonstration, the creation of the different data streams is done using CDK. See [the file](https://github.com/jbcodeforce/big-data-tenant-analytics/tree/main/setup/kinesis-cdk/app.py) and to deploy those streams, do a `cdk deploy` under the folder: [setup/kinesis-cdk](https://github.com/jbcodeforce/big-data-tenant-analytics/tree/main/setup/kinesis-cdk).
The persistence is set to 24 hours.


## Kinesis Data Analytics Code explanation

The code is under [rt-analytics/bg-job-processing](https://github.com/jbcodeforce/big-data-tenant-analytics/tree/main/rt-analytics/bg-job-processing) folder.
 
The main input stream includes the job events, and are published to Kinesis Data Streams names `bigdatajobs`. Once received we need to enrich with the company data, which lead to an asynchronous call to TenantManager service.
The Company response includes: company_id, industry, revenu, employees, job30, job90, monthlyFee, totalFee in a form of JSON document.

Once Company data is collected, the call to the ML scoring model is also done asynchronously, the churn flag may be update.
The final step is to write to S3 bucket.

The Flink data source is a KinesisConsumer and the declaration looks mostly always the same:

```java
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;

// in the main()

final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

env.addSource(new FlinkKinesisConsumer<>(inputStreamName,
                new SimpleStringSchema(),
                inputProperties));
```

## to rework

Joins between company and job streams on the company ID and add the number of jobs run (from job event) to the company current jobs count.

* Job is: company_id, userid , #job_submitted
* Out come is : company_id, industry, revenu, employees, job30 + #job_submitted, job90 + #job_submitted, monthlyFee, totalFee


## Deploy

We need to create a role and permission policy so the application can access source and sink resources:

```sh
aws iam create-role 
```

* We need to define permissions policy with two statements: one that grants permissions for the read action on the source streams, and another that grants permissions for write actions on the sink stream which will be S3 bucker and Data Streams:

```
```

* Attach the policy to an IAM role 

```
```

* Build the java packaging and upload it to S3

```sh
# build the uber jar
mvn package
# upload to S3
aws s3 cp $(pwd)/target/bg-job-processing-1.0.0.jar s3://jb-data-set/churn/bg-job-processing-1.0.0.jar
```

### Manual deployment

Using the Kinesis console to add an Analytics Application

![](./images/kda-config-job.png)

![](./images/kda-logging.png)

![](./images/kda-run.png)

### Deploy with AWS CLI

* Under rt-analytics folder modify the template for create_request-tmpl.json by changing bucket name, prefix and file name. 

```
aws kinesisanalyticsv2 create-application --cli-input-json file://create_request.json
```

## Clean up

```sh
aws kinesisanalyticsv2 stop-application --application-name CompanyJobProcessing --force 
aws kinesisanalyticsv2 describe-application --application-name CompanyJobProcessing | jq 
aws kinesisanalyticsv2 delete-application --application-name CompanyJobProcessing --create-timestamp 2022-12-23T17:02:09-08:00
```

```sh
aws s3 rm s3://jb-data-set/churn/bg-job-processing-1.0.0-runner.jar
```