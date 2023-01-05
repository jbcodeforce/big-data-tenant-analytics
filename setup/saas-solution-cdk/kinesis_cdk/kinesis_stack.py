from aws_cdk import (
    aws_kinesis as kinesis,
    aws_kinesisanalyticsv2 as kinesisanalytics,
    Duration,
    App, CfnOutput, Stack
)

class KinesisStack(Stack):
    def __init__(self, app: App, id: str) -> None:
        super().__init__(app, id)

        kinesis.Stream(self, "KinesisStreamJobs",
            stream_name="bigdatajobs",
            shard_count=1,
            retention_period=Duration.hours(24)
        )

        kinesis.Stream(self, "KinesisStreamCompanies",
            stream_name="companies",
            shard_count=1,
            retention_period=Duration.hours(24)
        )
        
        kinesis.Stream(self, "KinesisStreamEnrichedCompanies",
            stream_name="enrichedcompanies",
            shard_count=1,
            retention_period=Duration.hours(24)
        )

        