sklearn_estimator = SKLearn('sklearn-train.py',
                            instance_type='ml.m4.xlarge',
                            framework_version='0.20.0',
                            hyperparameters = {'epochs': 20, 'batch-size': 64, 'learning-rate': 0.1})
sklearn_estimator.fit({'train': 's3://my-data-bucket/path/to/my/training/data',
                        'test': 's3://my-data-bucket/path/to/my/test/data'})