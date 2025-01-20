## Publisher-Subscriber Pattern Using AWS SNS and SQS

The sample project acts as both a publisher to an SNS topic and a subscriber to the corresponding subscriber SQS queue.

The application exposes a single POST REST API `/api/v1/users` which when invoked publishes a message to the configured SNS topic.

The application also contains a listener class that polls the SQS queue sbuscribed to the configured topic and logs the recevied message.

For simplicity, we'll use a single IAM user that holds permission to perform all desired operations.

### Configuring Publisher
1. Create an SNS topic in the desired region
2. Create an IAM user
3. Configure the below policy to the created IAM user:

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

### Configuring Subscriber
1. Create a standard SQS queue in the desired region
2. Subscribe to the earlier created SNS topic
3. In the SQS queue policy, add the below `Statement` to allow the SNS topic to send messages to the queue:
    ```json
   {
      "Effect": "Allow",
      "Principal": {
          "Service": "sns.amazonaws.com"
      },
      "Action": "sqs:SendMessage",
      "Resource": "arn:aws:sqs:region-code:account-id:queue-name",
      "Condition": {
          "ArnEquals": {
              "aws:SourceArn": "arn:aws:sns:region-code:account-id:topic-name"
          }
      }
    }
    ```
4. Add the below `Statement` to the existing IAM user:
    ```json
    {
        "Sid": "SQSSubscriberPermission",
        "Effect": "Allow",
        "Action": [
            "sqs:ReceiveMessage",
            "sqs:DeleteMessage"
        ],
        "Resource": "arn:aws:sqs:region-code:account-id:queue-name"
    }
    ```
5. Configure the SQS queue URL and region in the `application.yaml` file.

### Integrating KMS to the architecture
1. Create a custom KMS symmetric key
2. Enable encryption on both our SNS topic and SQS queue by configuring them to use our newly created KMS key.
3. In the KMS Key policy, add the below two statements to allow both the SNS topic and SQS queue to use the key:
    ```json
    {
        "Effect": "Allow",
        "Principal": {
            "Service": "sqs.amazonaws.com"
        },
        "Action": [
            "kms:GenerateDataKey",
            "kms:Decrypt"
        ],
        "Resource": "arn:aws:kms:region-code:account-id:key/key-id",
        "Condition": {
            "ArnEquals": {
                "aws:SourceArn": "arn:aws:sqs:region-code:account-id:queue-name"
            }
        }
    }
    ```
    ```json
    {
        "Effect": "Allow",
        "Principal": {
            "Service": "sns.amazonaws.com"
        },
        "Action": [
            "kms:GenerateDataKey",
            "kms:Decrypt"
        ],
        "Resource": "arn:aws:kms:region-code:account-id:key/key-id",
        "Condition": {
            "ArnEquals": {
                "aws:SourceArn": "arn:aws:sns:region-code:account-id:topic-name"
            }
        }
    }
    ```
4. Add the below `Statement` to the existing IAM user:
    ```json
    {
        "Sid": "KMSSecurityCompliance",
        "Effect": "Allow",
        "Action": [
            "kms:GenerateDataKey",
            "kms:Decrypt"
        ],
        "Resource": "arn:aws:kms:region-code:account-id:key/key-id"
    }
    ```
   
### Integrating With Secrets Manager

For our demonstration, we'll store the SNS topic ARN and SQS queue URL to secrets manager and have our application fetch it during startup.

1. Create a new secret in Secrets Manager and configure the topic arn and queue url in the key/value pairs. Use the key names of SNS_TOPIC_ARN and SQS_QUEUE_URL.
2. Configure the secret-name in `application.yaml` file.
3. Add the below `Statement` to the existing IAM user:
    ```json
    {
        "Sid": "SecretFetcher",
        "Effect": "Allow",
        "Action": "secretsmanager:GetSecretValue",
        "Resource": "arn:aws:secretsmanager:region-code:account-id:secret:secret-name"
    }
    ```