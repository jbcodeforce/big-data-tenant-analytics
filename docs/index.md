# Big Data SaaS management

As an ISV delivering Big Data platform for their on-premises customers, AnyCompany wants to move to a SaaS model. They designed a new architecture with multi-tenant support. The new SaaS platform includes a set of microservices to register tenant, to manage account, billing, and specific components which support data catalog, and coordinate big data batch execution. 

All those components are generating data, and we want to propose to extend their SaaS architecture to leverage AWS services such as Kinesis, SageMaker, Quicksight to monitor their users activities and assess risk of churn. 

## Goal

The following business questions may be answered by using the new platform:

* how often tenant login, and work on data lake and then submit jobs
* which customers are not doing a lot of activities after logging
* what is the size of their data set
* how many batches are run per customer, per day
* can we identify the customers doing very minimum

## Scope

* build a simulator to generate clickstreams data: all those data elements have a timestamp
    * account created: company name and industry, sentiment about the company
    * user added, user deleted
    * user login, user logoff, user session timeoff
    * jobSubmitted, jobTerminated, jobCancelled


* Data elements to consider: number of server, data size
* Concentrates those events into kinesis streams: tenants, users, jobs
* Keep data for 7 days
* Move data for long term persistence to S3, bucket per tenant/user
* Use Sagemaker to develop a decision tree or random forest model to score risk of customer churn. The training set will be created by simulator so we can build a decision tree
* Deploy the model as sagemaker hosted service and integrate it into an agent that listening to events from jobs, users, tenant topics and score the risk of churn. Implement a Kinesis streams analytics with the logic of 
* Represent some metrics with QuickSight:
    * tenants, # of users
    * for a given tenant user activities over time
    * job submitted, cancelled, over time per tenant
    * last activity date per tenant
