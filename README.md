# dummy-smtp-server

A dummy SMTP server, the intent of this app is to replace the actual SMTP service in an application in order to be able 
to test its functionality in regards to SMTP requests, and check if the emails sent are actually what we intend to send.

All emails sent to dummy SMTP service are stored in memory and are mapped according to the destination email, this means 
that the service will override emails sent to the same destination. (Yes its a very dummy implementation of the email 
storage, but it does the trick for now, feel free to contribute and change it if it does not accommodate your flow)

**NOTE:** The server at the moment does not support authentication

## API
Dropwizard on top of the dummy STMP provides ways to get the emails sent through a simple API. 

At the moment there is one endpoint:

```
GET host:port/email?recipient={destination_email}
```
this endpoint will take the recipient and return the email that it was suppose to receive in a JSON format

```json
{
  "from" : {sender_email},
  "to" : {destination_email},
  "email" : {email_data}
}
```

in case the email was not found an empty JSON is returned.

## How to build the application?
The application is using `gradle application plugin` in order to build a deployable JAR. Run following commands 
in order to have a tar/zip built:

```bash
./gradlew clean
./gradlew copyConfig
./gradlew distTar
or
./gradlew distZip
```

After this commands you can find the build inside `./build/distribution` folder

## DOCKER
### How to build a docker image?
Docker file is ready to be used inside the project, you can configure it by changing following parameters:

```bash
ENV DUMMY_SMTP_SERVICE_PORT 9086 -  this is the API port of the application
ENV DUMMY_SMTP_SERVICE_ADMIN_PORT 9087 - this is the admin port of the API
ENV DUMMY_SMTP_SERVER_PORT 6969 - this is the dummy SMTP port
```

**NOTE:** don't forget to export the correct API and SMTP server ports

### Docker image publishing
In case you have a repository where you push your docker images then there is a simple script that will 
automate this the publishing:

1. make sure you logged in docker to the repository you are using
2. configure release bash:
```bash
IMAGE_NAME="dummy-smtp-service" - this sets the local docker image name
DOCKER_TAG="dummy-smtp-service" - this sets the tag which will be used when pushing the image to external repo
DOCKER_REPO="my/repo" - this is external repo path
```
3 . run `./docker-release.sh`