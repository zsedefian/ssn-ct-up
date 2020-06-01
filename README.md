# Discoverability Service
Discoverability service is an API responsible for cataloging platform services and their metadata (e.g., code repository links) to make it easy for developers to retrieve information about AV-TaaS platform services.

## Registration
Registering a platform service to discoverability consists of adding a custom CloudFormation resource to a service's stack.

This custom resource has a `Properties` field which must be filled out by the developer. 

* `ServiceToken` - ARN of DiscoverabilityServiceProvisioner lambda (SSM parameter available).   
* `Id` - Unique identifier for the service.
* `Type` - Type of service, e.g., ApiGateway, Lambda, etc.
* `Name` - Display name of the service.
* `Version` - Current service version.
* `Visibility` - Service visibility, e.g., Private, Tenant.
* `HealthCheckUri` (Optional) - URI of service health check.
* `AlertDestinationTopic` (Optional) - ARN of SNS topic alerts will be published to. Please see [operational-metrics](https://github.ford.com/AV-TaaS/operational-metrics) for more details.
* `DocumentationUri` - URI of OpenAPI 3.0 Specification file in service code repository. 
* `Endpoint` - URI of service's external interface.
* `CodeQualityUri` (Optional) - URI of code quality report (e.g., link to SonarQube).
* `PipelineName` (Optional) - Name of pipeline (e.g., `discoverability-service`). Will be converted to environment-appropriate URI 
before registering with discoverability. (Please note this deviates from the discoverability API spec because the URI 
must be built dynamically.)
* `CodeRepositoryUri` (Optional) - URI of code repository (e.g., link to Github)
* `ApiId` (Required if type is ApiGateway) - The ID of the API in API Gateway. (Must be excluded if type is not API Gateway.)
* `ApiStageName` (Required if type is ApiGateway) - The name of the deployed API stage. (Must be excluded if type is not API Gateway.)

Please note the required fields are `Id`, `Type`, `Name`, `Version`, `Visibility`, `DocumentationUri`, and `Endpoint`. If the type
is API Gateway, those required fields grow to include `ApiId` and `ApiStageName`.
For more information on the discoverability API please see the [OpenApi spec](/infrastructure/openapi-spec.yml).

An example of how to use the discoverability registration CloudFormation resource is located 
 in [samples/registration.yml](samples/registration.yml)

