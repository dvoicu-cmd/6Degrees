Hello. It's late and I don't want to spend another day on this, so I'll make this short.

This is my 6degress to Keven Bacon project. I'll briefly tell you some things to know that might make marking easier.

Where to find my java files:
All my java files should be under src/main/java/ca/yorku/eecs. In this path you will see the original App.java and Utils.java file + the HttpInitializer and Strategy folder.

Control flow/purposes of classes
Starting from App, you go to HttpInitializer. This acts as request's gateway into the Neo4jFacade in the Strategy folder. The Neo4JFacade sets up the Strategy pattern and calls the appropriate method given the request. The request is then processed in the appropriate strategy. The response back is then passed back with the class httpBundle.

Where to find test files:
The robot test file should be found under src/test

What is the new feature added:
The new feature I added was a PUT method called baconToAll. When called, the Keven Bacon node will have the ACTED_IN property attached to all movie nodes in the database. To test the method, check all movie nodes to see if Keven Bacon is attached to it. The method should fail when Keven Bacon is not in the DB or when there are no movies.

How to start:
Run app.java, and you should be good to run tests.

This took to long. Was it worth it to solo? I don't know... people are annoying to work with, but that's just me.

also, lmao this is not a .md file HAH