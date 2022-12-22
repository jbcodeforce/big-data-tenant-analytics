# Business DashBoard with QuickSight

## Business Insight Queries

Recall that the goals for this dashboard is to be able to answer to following questions:

* How often tenants work on data lake and then submit jobs?
* Which customers are not doing a lot of activities after logging?
* What is the size of their data set?
* How many batches are run per customer, per day?

## End Result

![](./images/qs-dashboard.png)

[Dashboard Link](https://us-west-2.quicksight.aws.amazon.com/sn/dashboards/1f7b933a-294e-4583-9ca4-a9fbabf5956a/sheets/1f7b933a-294e-4583-9ca4-a9fbabf5956a_aed7d0fd-c324-4f3b-87d9-97c8fa15a69c)

## Demonstration

* Go to the portal
* Explain each widgets
* Explain navigation

## Architecture

Business functions are illustrated in the figure below, with the SaaS big-data platform used by the SaaS customer's Data Sciences to run big data processing.

![](./diagrams/dashboard.drawio.png)

The job metadata and user's click streams are ingected to the SaaS data lake on which intelligent queries serve a BI dashboard.

If we take this business architecture and maps it with to AWS services we have the following figure:

![](./diagrams/qs-arch.drawio.png)

(See detail in [the design section](../design/#quicksight-integration-design))

## Build the dashboard

* Start QuickSight
* Modify policy so QuickSight can access the bucket where Stream Analytics output its job's outcome.
* Define a manifest file for accessing the S3 bucket and folders (See [these manifests](https://github.com/jbcodeforce/big-data-tenant-analytics/tree/main/qs-dashboard) as source).

    * The files need to have the same structure
    If the upload of the manifest fails with a criptic message, [see this note](https://docs.aws.amazon.com/quicksight/latest/user/troubleshoot-connect-S3.html)

* Create a Dataset from S3 bucket customer file and one Dataset for jobs file
* Change the Type of Date from String to Date
* Add one Analysis and be sure to add the second dataset.
* Add visualization


