## Persistence Layer With Amazon DynamoDB

The application uses Amazon DynamoDB as its persistence layer and exposes a `UserRepository` class that performs basic CRUD operations on the `User` entity.

The CRUD functionalities can be verified by running the integration tests:

```shell
mvn integration-test:verify
```

### Configuration Details

1. Create a DynamoDB table named `users` in the desired region.
2. Create an IAM user with the below policy:
    ```json
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Action": [
                    "dynamodb:PutItem",
                    "dynamodb:DeleteItem",
                    "dynamodb:GetItem",
                    "dynamodb:Scan",
                    "dynamodb:UpdateItem"
                ],
                "Resource": "arn:aws:dynamodb:region-code:account-id:table/users"
            }
        ]
    }
    ```
3. Configure the IAM security credentials along with the DynamoDB table region in the `application.yaml` file.