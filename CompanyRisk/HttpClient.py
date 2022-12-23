
import requests
SGENDPOINT="https://runtime.sagemaker.us-west-2.amazonaws.com/endpoints/linear-learner-<TOCHANGE>/invocations"

rep = requests.get(SGENDPOINT)
print(rep)