# ref-app

A reference application.

## Components

- [Scala](https://www.scala-lang.org/) / [Play](https://www.playframework.com/) backend
- Database connectivity with [Quill](https://getquill.io/) and migrations with [Flyway](https://flywaydb.org/)
- Server-rendered HTML with [ScalaTags](http://www.lihaoyi.com/scalatags/)
- Webpack-generated frontend assets in [frontend](frontend) including TypeScript and SASS
- [Selenium](http://www.scalatest.org/user_guide/using_selenium) integration tests in [SeleniumTests.scala](test/tests/SeleniumTests.scala)
- [buildspec.yml](buildspec.yml) for deployment to [AWS Elastic Beanstalk](https://aws.amazon.com/elasticbeanstalk/)

## Running

As prerequisites, install:

- [Java 11](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
- [sbt](https://www.scala-sbt.org/) 1.3.x
- [Node.js](https://nodejs.org/en/) 10.x

### Locally

To run the app:

    sbt run

The app should now run at http://localhost:9000.

A Play run hook in [NPMRunHook.scala](project/NPMRunHook.scala) launches `npm` when `sbt run` is started, therefore:

- both the backend and frontend are built at the same time
- any built frontend assets are made available to the Scala backend

### With Docker

Optionally, you can choose to run the app in a Docker container. Install Docker on your machine, then run:

    sbt docker:stage
    docker build --tag=hello target/docker/stage
    docker run -p 9000:9000 hello

The app should now run at http://localhost:9000.

## AWS and Deployments

The buildspec file in the root of the repo can be used with AWS CodeBuild and CodePipeline.

| Buildspec file | Deployment target
|----------------|------------------
| [buildspec.yml](buildspec.yml) | Single-container Elastic Beanstalk

To deploy the application to AWS along with a database and a CI pipeline:

1. Copy the CloudFormation templates in [infra](infra) to an S3 bucket
1. Deploy CloudFormation template [infra/vpc-bastion-aurora-eb-ci.cfn.yml](infra/vpc-bastion-aurora-eb-ci.cfn.yml)

## Formatting

Formatting is [configured](.scalafmt.conf) with [Scalafmt](https://scalameta.org/scalafmt/). Use "Format on save" in your IDE.
