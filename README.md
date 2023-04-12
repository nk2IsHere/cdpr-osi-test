# cdpr-osi-test
CDPR Online Services Internship test task solution

## Functionality

### Implemented

- User domain with APIs for user create, user self information update and user self credentials update
- System user creation on application startup
- Tag domain with API for tags search, tags create and update
- Game domain with APIs for game create and update, search and query
- Token domain with JWT token generation for user authorization
- OpenAPI generated specification, available at - <server url>/webjars/swagger-ui/index.html

### Unimplemented (discussed in per-domain review)

- Game search by tags with pagination
- Caching on high-intensity operations
- Integration tests for APIs

## Technology stack

### Application stack

- Java 17 with features preview
- Lombok
- Spring Boot
- Spring Webflux
- Spring Data R2DBC
- Postgres
- Liquibase

### Build/Test system

- Gradle
- JUnit

## Structure of an application

### General remarks

- Project follows Domain Driven Approach - every featureset is located in its separate high cohesion "domain" modules. 
- Every domain has the implementation internal part located in the root of the domain package which implements the protocol interface (protocol package).
- Every domain has the context package used for infrastructure (like API) and dependencies.
- Every domain that wants to expose an API must define it in the DomainRouterConfiguration as a functional router bean.
- The API router layer is logic-less, i.e. it should only prepare the request for service layer and handle the response.
- The service layer uses functional approach for business logic with sealed types to determine the result of the call. This allows to explicitly state all ending conditions of the call and removes exception handling from system.

### Common domain

- This domain defines utilities used in application and a security web filter factory used in API Routers in other domains.
- Spring security is not used throughout the application as the functional web filters have poor support for it and the concept of security "magic" does not conform with general idea of functional and explicit approaches in the architecture of the app.

### Token domain

- This domain defines a toolkit for credentials generation and validation.
- JWT is used as a POC of such domain implementation
- This domain depends on configuration properties:
  - cdprshop.token.token-expiration-millis - milliseconds until token expires. can be null.
  - private.key.der classpath resource - RS256 private sign key for JWT 
  - public.key.der classpath resource - PR256 public sign key for JWT

### User domain

- This domain defines a business logic related to user information and credentials management.
- Implemented features:
  - System user creation on start from properties
  - Admin user management (create, update, delete) by users of role ADMIN and SYSTEM
  - Credentials management (authentication, update) by any users

### Tag domain

- This domain defines a business logic related to tag information management and querying. 
- Tags are used in Game entity to defined snippets on searchable typed non-unique information about Game.
- Implemented features:
  - Tag management (create, delete) by users of role ADMIN and SYSTEM
  - Tag querying (get by id, find all sorted paged) by any users or anonymous

### Game domain

- This domain defines a business logic related to game information management and querying.
- Game is not a normalized entity as it stores tags as an embedded entity (similar to nosql document approach) for faster querying and resilience in changes.
- Implemented features:
  - Game management (create, update, delete) by users of role ADMIN and SYSTEM
  - Game querying (get by id, find all sorted paged) by any users or anonymous
  - Game searching (by title, by description, tags unimplemented) by any users or anonymous

Remark: Game searching featureset uses Postgres full text search functionality with json search for tags.

### Future prospects

- Unfortunately one of the requirements - code is covered with tests - is not finished in time for submission. The importance of tests in such project is undeniable but it is currently left out of scope of the project due to time constraints.
- TDD approach was not used when developing this project - the usage of the approach may have eliminated the drawback described in the first point.
- Caching (using Redis for example) can be implemented for Game searching featureset as it is computationally intensive and the data for search does not require updates as soon as possible when data changes.
