## Storage Service on Top of Amazon S3

The sample project exposes REST APIs to communicate with a provisioned S3 bucket to perform basic CRUD operations on file objects.

The integration-test to verify the storage service functionality can be run using the following command:

```shell
mvn integration-test:verify
```

Alternatively, the Testcontainers for development support can be used to start the application and test the APIs:

```shell
mvn spring-boot:test-run
```

### AWS Configuration
1. Create an S3 bucket in the desired region

2. Create an IAM user

3. Configure the below policy to the created IAM user:

    ```json
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Action": [
                    "s3:GetObject",
                    "s3:PutObject", 
                    "s3:DeleteObject"
                ],
                "Resource": "arn:aws:s3:::bucket-name/*"
            }
        ]
    }
    ```
4. Configure the IAM user security credentials and the s3 bucket name in the `application.yaml` file.