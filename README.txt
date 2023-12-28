Hello. I felt that this project is a good representation of how I am able to apply oop design paterns, so I've decided to make it public.

In short: I had to design a REST API that runs on a java server that would interface with a neo4j database. The goal of the API is to organize nodes in the database and ultimate return a path from any actor node to the keven bacon node.
Here is a useful link for more context on the problem: https://en.wikipedia.org/wiki/Six_Degrees_of_Kevin_Bacon

Below is my original read me to the TA for this project.
---------------------------------------------------------

Hello, I'll make this short.

This is my 6degress to Keven Bacon project. I'll briefly tell you some things to know that might make marking easier.

Where to find my java files:
All my java files should be under src/main/java/ca/yorku/eecs. In this path you will see the original App.java and Utils.java file + the HttpInitializer and Strategy folder.

Control flow/purposes of classes:
To start the server, run the App,java file. From app, you then go to HttpInitializer. This acts as request's gateway into the Neo4jFacade in the Strategy folder. The Neo4JFacade sets up the Strategy pattern and calls the appropriate method given the request. The request is then processed in the appropriate strategy. The response back is then passed back with the class httpBundle.

Where to find test files:
The robot test file should be found under src/test

What is the new feature added:
The new feature I added was a PUT method called baconToAll. It can be called with: http://localhost:8080/api/v1/baconToAll . When called, the Keven Bacon node will have the ACTED_IN property attached to all movie nodes in the database. To test the method, check all movie nodes to see if Keven Bacon is attached to it. The method should fail when Keven Bacon is not in the DB or when there are no movies.

How to start:
Run app.java, and you should be good to run tests.

This took to long. Was it worth it to solo? I don't know... people are annoying to work with, but that's just me. Also, this is not a .md file, cry about it. (Don't check my git logs)
