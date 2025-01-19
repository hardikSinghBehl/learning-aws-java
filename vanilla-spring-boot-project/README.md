## Deploying Spring Boot Application to AWS
This is a basic Spring Boot application that exposes a single GET API endpoint at `/api/v1/users` on port 8080.

### Deploying to EC2 via Git
The following user data script can be used to deploy the application to EC2:
```bash
#!/bin/bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 23-open
sdk install maven
dnf install -y git-all
git clone https://github.com/hardikSinghBehl/upwork-lessons-with-stef.git
cd upwork-lessons-with-stef/vanilla-spring-boot-project
mvn spring-boot:run &
```
In this script, we first install [SDKMAN](https://sdkman.io/) on the system, which we then use to install the latest versions of Java and Maven.
Next, we install Git, clone the repository, and start the application.

We'll need to allow access to port `8080` in the attached security group.

### Deploying to EC2 using an S3 Bucket
First, run the command `mvn clean install` in the base directory of your project. Then, upload the executable JAR created in the `target` directory to an S3 bucket.

The following user data script can be used to deploy the application:
```bash
#!/bin/bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 23-open
aws s3 cp s3://bucket-name/vanilla-project-0.0.1-SNAPSHOT.jar .
java -jar vanilla-project-0.0.1-SNAPSHOT.jar
```
We'll also need to attach an IAM role to the provisioned EC2 instance that provides permission to [copy objects](https://docs.aws.amazon.com/AmazonS3/latest/API/API_CopyObject.html) from the S3 bucket.

In this approach, we again install SDKMAN but only install Java since we already have the executable JAR in our S3 bucket. We use the AWS CLI (pre-installed on EC2 instances) to copy the file to the system and then execute it.
