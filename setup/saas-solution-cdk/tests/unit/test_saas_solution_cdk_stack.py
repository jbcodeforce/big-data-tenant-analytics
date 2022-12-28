import aws_cdk as core
import aws_cdk.assertions as assertions

from saas_solution_cdk.saas_solution_cdk_stack import SaasSolutionCdkStack

# example tests. To run these tests, uncomment this file along with the example
# resource in saas_solution_cdk/saas_solution_cdk_stack.py
def test_sqs_queue_created():
    app = core.App()
    stack = SaasSolutionCdkStack(app, "saas-solution-cdk")
    template = assertions.Template.from_stack(stack)

#     template.has_resource_properties("AWS::SQS::Queue", {
#         "VisibilityTimeout": 300
#     })
