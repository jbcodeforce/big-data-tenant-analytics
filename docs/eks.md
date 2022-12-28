# Elastic Kubernetes Service

[Amazon EKS](https://aws.amazon.com/eks/) is a fully managed service to run Kubernetes. 

![](./diagrams/eks-ec2.drawio.svg)

It is integrated with VPC for isolation, IAM for authentication, ELB for load distribution, and ECR for container image registry.

## Major characteristics

* Scale K8s control plane across multiple AZs.
* No need to install, operate and maintain k8s cluster.
* Automatically scales control plane instances based on load, detects and replaces unhealthy control plane instance.
* It supports EC2 to deploy worker nodes or Fargate to deploy serverless containers or [on to AWS Outposts](../../infra/#aws-outposts).
* Fully compatible with other CNSF kubernetes

## Create a cluster with CDK

To use an infrastructure as code, we use CDK to create a EKS cluster. The AWS CDK revolves around a fundamental building block called a construct. These constructs have three abstraction levels:

* L1 – A one-to-one mapping to AWS CloudFormation
* L2 – An intent-based API
* L3 – A high-level pattern

Under `setup/eks-cdk` run the command `cdk deploy`.

