import os
import json
import argparse
import numpy as np
import pandas as pd
from pandas.api.types import CategoricalDtype
import joblib
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
#from sagemaker import get_execution_role
#import sagemaker

MODEL_NAME="churn-scoring-model.joblib"

# training code in a main guard, with model deployment once fitted
if __name__ == '__main__':
    parser = argparse.ArgumentParser()

    # hyperparameters sent by the client are passed as command-line arguments to the script.
    parser.add_argument('--epochs', type=int, default=10)
    parser.add_argument('--batch-size', type=int, default=100)
    parser.add_argument('--learning-rate', type=float, default=0.1)
    # input data and model directories
    parser.add_argument('--model-dir', type=str,
                        default=os.environ['SM_MODEL_DIR'])
    parser.add_argument('--train', type=str,
                        default=os.environ['SM_CHANNEL_TRAIN'])
    parser.add_argument('--test', type=str,
                        default=os.environ['SM_CHANNEL_TEST'])
    parser.add_argument("filename", type=str, default="companies.csv")

    args, _ = parser.parse_known_args()
    # Prepare data
    dataset = pd.read_csv(os.path.join(args.train, args.filename))
    industryCat = dataset["Industry"].unique()
    industry_type = CategoricalDtype(categories=industryCat)
    dataset['Industry'] = dataset['Industry'].astype(industry_type)
    dataset['Churn'] = dataset['Churn'].astype('bool')
    dataset.drop(['Company'], axis=1, inplace=True)
    dataset = pd.get_dummies(dataset, columns=['Industry'])

    X_train, X_test, y_train, y_test = train_test_split(
        # data to use
        dataset.drop('Churn', 1),
        # target column
        dataset['Churn'],
        test_size=.2,
        random_state=10)

    model = RandomForestClassifier(max_depth=5, n_estimators=2)
    model.fit(X_train, y_train)
    print("validating model")
    abs_err = np.abs(model.predict(X_test) - y_test)
    print(abs_err)
    path = os.path.join(args.model_dir, MODEL_NAME)
    joblib.dump(model, path)
    print("model persisted at " + path)

# Callback for SageMaker Scikit-learn to load the model... from s3
def model_fn(model_dir):
    return joblib.load(os.path.join(model_dir, MODEL_NAME))

'''

# Process input request, build a input_object compatible with the model
def input_fn(request_body,request_content_type):
    pass

def predict_fn(input_object,model):
    pass

def output_fn(prediction,response_content_type):
    pass

'''