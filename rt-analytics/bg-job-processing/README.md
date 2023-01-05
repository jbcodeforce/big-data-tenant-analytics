# Job processor

## Use cases supported

* Joins between company and job streams on the company ID and add the number of jobs run (from job event) to the company current jobs count.

    * Company event is a csv with: company_id, industry, revenu, employees, job30, job90, monthlyFee, totalFee
    * Job is: company_id, userid , #job_submitted, timestamp
    * Out come is : company_id, industry, revenu, employees, job30 + #job_submitted, job90 + #job_submitted, monthlyFee, totalFee

## Packaging

The application can be packaged ( build an _über-jar_) using:

```shell script
./mvnw package
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Run the app in Flink locally

* Start Flink Job manager and job runner: `docker compose up -d`
* Get docker id for the job manager

```sh
export JMC=$(docker ps --filter name=jobmanager --format={{.ID}})
```

* Specify the Java class to execute

```sh
export CNAME=jbcodeforce.bgdatajob.FileBasedJobProcessor
```

* Send Job to the Job manager

```sh
docker exec -ti $JMC flink run -d -c $CNAME /home/bg-job-processing/target/bg-job-processing-1.0.0-runner.jar --companies file:///home/bg-job-processing/data/companies.csv --jobs --jobs file:///home/bg-job-processing/data/jobs.csv --output file:///home/bg-job-processing/data/ouput.txt

# OR
./sendJobToManager.sh 
```

## Deploy to Kinesis

Once the code is packaged with `mvn package`, upload the jar to S3 bucket with:

```sh
aws s3 cp $(pwd)/target/bg-job-processing-1.0.0.jar s3://jb-data-set/churn/bg-job-processing-1.0.0.jar
```



