# Legacy Spring Batch

This project is a demo from my I Code Java 2016 talk entitled "Stream is the new batch"

The application is a trivial Spring Batch application that reads a file and capitalises
each line, printing the result to the log.

## Tomcat

This application is deployed to a Tomcat instance to demonstrate how current batch applications are
deployed in a "legacy" fashion.

### Running a Tomcat instance

We will use Docker to run a [Tomcat](https://hub.docker.com/r/_/tomcat/) instance that we will deploy the application into.
Due to [this issue](https://github.com/docker-library/tomcat/issues/15#issuecomment-234468452)
with the Tomcat 8.5 image, we will use the [tomcat:8.0-jre8-alpine](https://hub.docker.com/r/library/tomcat/tags/) image.

We need to add an `admin` user that we will use to authenticate against when using the Maven Tomcat plugin to deploy
the application using the `/manager` resource. This means we need to customise the default
Tomcat image by adding a `tomcat-users.xml` file.

First, build the image:

```
$ cd src/main/docker && docker build -t kitty . 
```

Now we can run it:

```
$ docker run -it --rm -v /tmp/icodejava:/tmp/icodejava -p 8080:8080 kitty
```

Note that we mount the `/tmp/icodejava` directory on our local machine 
to the `/tmp/icodejava` directory inside the container. This is where we will
reference the file to process with our batch application.

## Deploying application

To deploy the application we will use the Maven Tomcat plugin:

```
$ mvn -s .settings.xml tomcat7:redeploy
```

## Running the batch

First we will copy over the file that our batch application will process:

```
$ mkdir -p /tmp/icodejava
$ cp batch/input.txt /tmp/icodejava 
```

Now we can trigger the batch processing with a `POST` to the trigger resource:
 
```
$ curl -X POST -H "Content-Type: text/plain" -d '/tmp/icodejava/input.txt' http://localhost:8080/batch
```

The `curl` call should block and you'll see the following in the Tomcat log:

```
... INFO  i.code.java.batch.BatchResource - Triggering batch job for: /tmp/icodejava/input.txt
... INFO  i.c.java.batch.JobConfiguration - Oh my word -> I
... INFO  i.c.java.batch.JobConfiguration - Oh my word -> CODE
... INFO  i.c.java.batch.JobConfiguration - Oh my word -> JAVA
... INFO  i.c.java.batch.JobConfiguration - Oh my word -> 2016
... INFO  i.c.java.batch.JobConfiguration - Oh my word -> LEGACY
... INFO  i.c.java.batch.JobConfiguration - Oh my word -> BATCH
... INFO  i.c.java.batch.JobConfiguration - Oh my word -> DEMO
... INFO  i.code.java.batch.BatchResource - Started job: JobExecution: id=6, version=2, startTime=..., endTime=..., lastUpdated=..., status=COMPLETED, exitStatus=exitCode=COMPLETED;exitDescription=, job=[JobInstance: id=4, version=0, Job=[uppercase]], jobParameters=[{filename=/tmp/icodejava/input.txt}]
```

