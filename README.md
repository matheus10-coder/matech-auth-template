# Getting Started

## Auth-Template - String Security w/ JWT
<p>
    This template is created to assist developers w/ an easy way to inject Spring Security to their projects utilizing JWT tokens and 
    AuthO. It demonstrates how to implement authentication in a Spring Boot application using Spring Security and JSON Web 
    Token. It includes user registration, authentication, and securing endpoints with JWT tokens. The project uses a 
    PostgresSQL database to demo User creation.
</p>
<p>
    The project is built with modern Java practices and uses Spring Boot for rapid development, Spring Security for 
    robust authentication and authorization, and JWT for stateless token-based authentication. It serves as a learning
    tool for developers new to securing REST API(s).
</p>

### Spring Security Framework Overview
[Architecture Diagram](src/main/resources/Spring%20Security%20Architecture%20Diagram.png)

### Tech Used
- <b> Spring Boot: </b> Framework for building the application.
- <b> Spring Security: </b> Handles authentication and authorization.
- <b> JWT: </b> Enables token-based stateless authentication.
- <b> PostgresSQL: </b> Docker image generated with {matech} local db connection.
- <b> Lombok: </b> Reduces boilerplate code with annotations like <code>@Data</code> and <code>@Builder</code>.

### Features
- <b> User Registration: </b> Create users with <code>firstName</code>, <code>lastName</code>, <code>email</code> and 
  <code>password</code>.
- <b> User Authentication: </b> Authenticate users and return a JWT token.
- <b> Secured Endpoints: </b> Protect endpoints with JWT-based authentication.
- <b> Basic Role-Based Access: </b> All users are assigned the <code>USER</code> role by default; extensible for multiple
  roles like <code>ADMIN</code>.

### Project Structure
The project is organized into the following key components under the <i>com.matech.auth</i> package:

| Component                             | Description                                                                                                                                                                                                    |
|---------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| <b>Controllers</b>                    |                                                                                                                                                                                                                |
| <code>AuthenticationController</code> | Handles [/api/v1/auth/register](http://localhost:8080/api/v1/auth/register) and [/api/v1/auth/authenticate](http://localhost:8080/api/v1/auth/authenticate) endpoints for user registration and authentication |
| <code>AppController</code>            | Example of a secured endpoint at [/api/v1/app-controller](http://localhost:8080/api/v1/app-controller)                                                                                                         |
| <b>Services</b>                       |                                                                                                                                                                                                                |
| <code>AuthenticationService</code>    | Contains business logic for user registration and authentication, including password encoding and JWT generation                                                                                               |
| <code>JwtService</code>               | Manages JWT token generation and validation, including extracting claims and verifying signatures                                                                                                              |
| <b>Filters</b>                        |                                                                                                                                                                                                                |
| <code>JwtAuthenticationFilter</code>  | Intercepts requests to validate JWT tokens and set authentication in the security context                                                                                                                      |
| <b>Configurations</b>                 |                                                                                                                                                                                                                |
| <code>ApplicationConfig</code>        | Defines Spring beans for <code>UserDetailsService</code>, <code>AuthenticationProvider</code>, <code>PasswordEncoder</code> and <code>AuthenticationManager</code>                                             |
| <code>SecurityConfig</code>           | Configures Spring Security settings, including stateless session management and endpoint permissions                                                                                                           |
| <b>Entities and Repositories</b>      |                                                                                                                                                                                                                |
| <code>User</code>                     | Entity class implementing UserDetails for Spring Security integration, mapped to the database                                                                                                                  |
| <code>UserRepository</code>           | JPA Repository for access user data, with a method to find users by email                                                                                                                                      |
| <b>DTOs</b>                           |                                                                                                                                                                                                                |
| <code>AuthenticateRequest</code>      | Data Transfer Object for authentication requests (email, password)                                                                                                                                             |
| <code>JwtAuthenticationFilter</code>  | Data Transfer Object for registration requests (firstName, lastName, email, password)                                                                                                                          |
| <code>JwtAuthenticationFilter</code>  | Data Transfer Object for returning JWT tokens                                                                                                                                                                  |


## To Run The App
1) Create/Connect data source (optional create your own)
    - start docker <or local>
        - run the image postgres-test-api
    - local postgres db <matech>
    - User: <matech>
    - Password: <password>
    - Database: <jwt_security schema:public>
2) Set app/jpa/configuration application.yml
    - spring
        - datasource
            - urL | username | password | driver-class-name
    - jpa
        - hibernate
            - ddl-auto: create-drop
        - show-sql:
        - properties:
            - hibernate:
                - format_sql: true
            - database: postgresql
            - database-platform: # write better queries to suit the choice of db

## Run Application - Authentication Flow
### Registration
- <b>Endpoint</b>: <code>POST: /api/v1/auth/register</code>.
- <b>Request Body</b>:
    ```json
      {
        "firstName": "Matheus",
        "lastName": "Ribeiro",
        "email": "matheus.ribeiro@matech.com",
        "password": "password"
      }
    ```
- <b>Process</b>
    - The <code>AuthenticationController</code> receives the request and passes it to <code>AuthenticationService</code>.
    - The service creates a <code>User</code> entity, encodes the password using <code>BCryptPasswordEncoder</code> and
      assigns the <code>USER</code> role.
    - The user is saved to the database via <code>UserRepository</code>.
    - A JWT token is generated using JwtService and returned in an <code>AuthenticationResponse</code>.

### Authentication
- <b>Endpoint</b>: <code>POST /api/v1/auth/authenticate </code>.
- <b>Request Body</b>:
    ```json
      {
        "email": "matheus.ribeiro@matech.com",
        "password": "password"
      }
    ```
- <b>Process</b>:
    - The <code>AuthenticationController</code> passes the request to <code>AuthenticationService</code>.
    - The service uses <code>AuthenticationManager</code> to verify the credentials.
    - If valid, if fetches the user from <code>UserRepository</code>, generates a JWT token, and returns it in an <code>AuthenticationResponse</code>.

### Accessing Secured Endpoints
- <b>Endpoint</b>: <code>GET: /api/v1/app-controller</code>.
- <b>Header</b>: Include the JWT token in the <code>Authorization</code> header starting with "Bearer".
- <b>Process</b>:
    - The <code>JwtAuthenticationFilter</code> checks for the <code>Authorization</code> header starting with "Bearer"
    - It extracts the token and uses <code>JwtService</code> to validate it and extract the username (email).
    - If valid, it fetches user details via <code>UserDetailService</code>, sets the authentication in the security context,
      and allows access.
    - The <code>AppController</code> return a success (200) response.

### Summary-Flow
<p> 
    Unauthenticated users will receive 403 for any endpoint request. For this application to work properly you must 
    follow the "happy path": <code>Register-User</code> -> <code>Authenticated-User</code> receives <code>Jwt-Token</code>
    A jwt token is used successfully to enter the secured home page. If the user is not found, consider create the user 
    first with the user details and create a new password. This will generate a valid token for 24 hrs to be used for 
    any session. If the user is already created please go to the authenticate endpoint and generate a new token and use 
    it as bearer token in order to access the app secured home.
</p>

## Security Configuration
The <code>SecurityConfig</code> class configures Spring Security:
- <b>CSRF</b>: Disabled, as REST API(s) are typically stateless and JWT handles authentication.
- <b>Endpoint Permissions</b>:
    - <code>/api/v1/auth/**</code>: Open to all for registration and authentication.
    - All other endpoints: Require authentication.
- <b>Session Management</b>: Set to <code>STATELESS</code>, as JWT eliminates the need for server-side sessions.
- <b>Authentication Provider</b>: Uses a custom <code>DaoAuthenticationProvider</code> with <code>UserDetailsService</code>
  user lookup and <code>BCryptPasswordEncoder</code> for password verification.
- <b>JWT Filter</b>: The <code>JwtAuthenticationFilter</code> is added before <code>UsernamePasswordAuthenticationFilter</code>
  to validate JWT tokens for each request.

<b>Extending Role-Based Access</b>: Currently, all authenticated users can access secured endpoints. To implement role-based
access control, modify <code>SecurityConfig</code> to include role checks, e.g.:
```java
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/auth/**")
                        .permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") //add here
                        .anyRequest()
                        .authenticated());
        return http.build();
    }
   ```

## JWT Implementation
The <code>JwtService</code> class handles JWT operations:
- #### Token Generation:
    - Sets the user's email as the subject.
    - Includes issuance time and a 24h expiration.
    - Signs the token with a secret key using the HS256 algorithm.
- #### Token Validation:
    - Extracts the username (email) from the token.
    - Checks if the token is expired.
    - Verifies the token against the user's details.
- #### Key Details:
    - The secret key is hardcoded for demo reasons but should be probably held in a key store (<code>SECRET_KEY</code> in <code>JwtService</code>).
        - <i><b>Important</b>: In production, do not hardcode the secret key. Store it securely using environment variables .
          or a secret management system (e.g., AWS Secrets Manager).</i>
    - Tokens expire after 24 hours, adjustable via <code>JwtService.generateToken</code>.

The <code>JwtAuthenticationFilter</code>:
- Checks for the <code>Authorization</code> header with "Bearer".
- Extracts and validates the token using <code>JwtService</code>.
- If valid, sets the authentication in <code>SecurityContextHolder</code> for the request.



# Additional Resources
## Spring Framework Docs

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.5/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.5/maven-plugin/build-image.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.5/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Security](https://docs.spring.io/spring-boot/3.4.5/reference/web/spring-security.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.5/reference/web/servlet.html)

### Guides
The following guides illustrate how to use some features concretely:
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Maven Parent overrides
Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.
