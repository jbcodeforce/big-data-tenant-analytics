# Real-time analytics with Kinesis Data Analytics

The goal of this component is to compute stateful analytics, do data transformation, from the data coming in streams. The current implementation illustrates remote async calls to SageMaker (via API Gateway) and persistence to S3.

![](./diagrams/qs-arch.drawio.png)

## Kinesis Data Streams

There is nothing special in this demonstration, the creation of the two streams are done using CDK. See [the file](app.py) and to deploy those streams, do a `cdk deploy` under the folder: [setup/kinesis-cdk]().

## Code explanation

Code is under [rt-analytics](https://github.com/jbcodeforce/big-data-tenant-analytics/tree/main/rt-analytics/bg-job-processing) folder.
 
The main input stream is the job events, it is published to Kinesis Data Streams names `bigdatajobs`. Once received we need to enrich with the company data, which lead to an asynchronous call to TenantManager service.
The Company response includes: company_id, industry, revenu, employees, job30, job90, monthlyFee, totalFee in a form of JSON

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

```sh
# build the uber jar
mvn package
# upload to S3
aws s3 cp $(pwd)/target/bg-job-processing-1.0.0-runner.jar s3://jb-data-set/churn/bg-job-processing-1.0.0-runner.jar
```

## Clean up

```sh
aws s3 rm s3://jb-data-set/churn/bg-job-processing-1.0.0-runner.jar
```