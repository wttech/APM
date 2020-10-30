## API Definition
#### Introduction
API definition is created with Open Api Standard 3.0
It can be used with Swagger 2.0

#### Swagger installation
To install Swagger:

##### Docker image
This should install swagger at the port which you passed in the configuration.

    https://github.com/swagger-api/swagger-ui/blob/master/docs/usage/installation.md

##### NPM local install
This should install swagger at `http://localhost:3200`

    git clone https://github.com/swagger-api/swagger-ui.git
    cd swagger-ui
    npm run dev

#### Swagger Inspector
There is a way to generate API definition, however it is not perfect, and some things have to be still fixed manually.
Swagger offers a tool which sends a request to the given URL, and given headers, parameters, and body.
Tool is available here: 

    https://inspector.swagger.io/builder#

After logging in to your account, you will be able to generate collections & API definitions for executed request.
The history of requests is assigned to your account, so you can always get back to previous requests.

#### Known issues
Due to CORS, the "Try it out" functionality doesn't work in swagger, if the swagger is hosted outside of AEM.
There are three solutions for that:
* Send proper CORS header from your AEM
* Turn off web-security in Chrome/Firefox browsers
* Host your swagger in AEM instance (there are open source projects which allows to do that)