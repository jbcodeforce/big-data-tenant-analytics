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
