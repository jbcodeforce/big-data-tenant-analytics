import unittest
from unittest import mock

with mock.patch.dict('os.environ', {'AWS_REGION' : 'us-west-2'}): import lambda_handler


class TestCompanyScoring(unittest.TestCase):
    def prepare_event(self):
        return {
            "Records" :[
                
            ]
        }