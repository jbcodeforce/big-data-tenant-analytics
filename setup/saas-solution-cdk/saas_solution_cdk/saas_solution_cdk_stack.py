from aws_cdk import (
    # Duration,
    Stack, Fn,
    aws_rds,
    aws_iam,
    aws_ec2,
    aws_eks,
    RemovalPolicy, SecretValue,
    aws_lambda,
    aws_apigateway as apigw
)

from constructs import Construct
cidr="10.10.0.0/16"
key_name = "my-key-pair"

class SaasSolutionCdkStack(Stack):

    def __init__(self, scope: Construct, construct_id: str, **kwargs) -> None:
        super().__init__(scope, construct_id, **kwargs)

        azs = Fn.get_azs()
        self.vpc = aws_ec2.Vpc(self, "VPC",
                           max_azs=2,
                           ip_addresses=aws_ec2.IpAddresses.cidr(cidr),
                           nat_gateways=2,
                           enable_dns_hostnames=True,
                           enable_dns_support=True,
                           subnet_configuration=[
                               aws_ec2.SubnetConfiguration(
                                   name="public",
                                   subnet_type=aws_ec2.SubnetType.PUBLIC,
                                   cidr_mask=24),
                               aws_ec2.SubnetConfiguration(
                                   subnet_type=aws_ec2.SubnetType.PRIVATE_WITH_EGRESS,
                                   name="private",
                                   cidr_mask=24) # could be /16 to have more instances, but this is a demo scope.
                           ]
                           )
        
        postgres = aws_rds.DatabaseInstance(self, "PostgresqlInstance",
                                database_name="tenantdb",
                                engine=aws_rds.DatabaseInstanceEngine.postgres(version=aws_rds.PostgresEngineVersion.VER_14_5),
                                vpc_subnets=aws_ec2.SubnetSelection(subnet_type=aws_ec2.SubnetType.PRIVATE_WITH_EGRESS),
                                vpc=self.vpc,
                                port=5432,
                                removal_policy=RemovalPolicy.DESTROY,
                                deletion_protection=False,
                                max_allocated_storage=200,
                                publicly_accessible=True
                        )
        
        # Create an IAM role for worker groups and kubernetes RBAC configuration
        self.eks_admin_role = aws_iam.Role(self, 'eksAdmin',
                                    assumed_by=aws_iam.ServicePrincipal(service='ec2.amazonaws.com'),
                                    role_name='eks-cluster-role', 
                                    managed_policies=
                                        [aws_iam.ManagedPolicy.from_aws_managed_policy_name(managed_policy_name='AdministratorAccess')])
        self.eks_instance_profile = aws_iam.CfnInstanceProfile(self, 'instanceprofile',
                                                      roles=[self.eks_admin_role.role_name],
                                                      instance_profile_name='eks-cluster-role')
                                      
    
                            

        cluster = aws_eks.Cluster(self, 'demo-cluster',
                                  masters_role=self.eks_admin_role,
                                  vpc=self.vpc,
                                  default_capacity=2,
                                  vpc_subnets=[aws_ec2.SubnetSelection(subnet_type=aws_ec2.SubnetType.PRIVATE_WITH_EGRESS)],
                                  version=aws_eks.KubernetesVersion.V1_24,
                                  output_cluster_name=True
                                  )


