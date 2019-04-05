# dynamicInterpreter
  This project is a Springboot application that interpret javascript and python languages .
   # Example :
   to execute a command by the app we use the `/execute` endpoint that accepts a JSON object such as:
    {
    “code”: “% python print 1+1”
    }
    The endpoint parse this input and compute what the output of the python program is.
    The code is formatted like this:
    
      %<interpreter-name><whitespace><code>

   -The returned output should be:
    {
    “result”: “2”
    }
  
   # Technologies :
   - SpringBoot
   - Mockito && JUNIT

  # Installing
  to run the app on your local execute the following commands:
  
    git clone https://github.com/Arrad92/dynamicInterpreter
    cd dynamicInterpreter
    ./mvnw spring-boot:run
   # Test
   to run all the unit tests :
   
      mvn test
   
   to run the Integration Tests:
   
      mvn -DTest=JSInterpreterIntegrationTests test
      mvn -DTest=PyInterpreterIntegrationTests test
