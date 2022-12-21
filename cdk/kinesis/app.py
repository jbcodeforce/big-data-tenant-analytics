#!/usr/bin/env python3

from aws_cdk import (
    aws_kinesis as kinesis,
    Duration,
    App, CfnOutput, Stack
)

class KinesisStack(Stack):
    def __init__(self, app: App, id: str) -> None:
        super().__init__(app, id)

        kinesis.Stream(self, "SaaSdemoStream",
            stream_name="bg-jobs",
            shard_count=1,
            retention_period=Duration.hours(24)
        )



app = App()
KinesisStack(app,"KinesisStack")
app.synth()