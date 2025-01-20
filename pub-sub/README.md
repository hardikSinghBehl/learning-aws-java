## Publisher-Subscriber Pattern Using AWS SNS and SQS

The sample project acts as both a publisher to an SNS topic and a subscriber to the corresponding subscriber SQS queue.

### Configuring Publisher
1. Create an SNS topic in the desired region
2. Create an IAM user
3. Configure the below policy to the created IAM user

    ```json
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Sid": "SNSPublisherPermission",
                "Effect": "Allow",
                "Action": [
                    "sns:Publish"
                ],
                "Resource": [
                    "arn:aws:sns:region-code:account-id:topic-name"
                ]
            }
        ]
    }
    ```
4. Configure the IAM user security credentials, SNS topic region and ARN in the `application.yaml` file.