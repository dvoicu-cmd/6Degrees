package ca.yorku.eecs.Strategy.PUT;

import ca.yorku.eecs.Strategy.RESTStrategy;
import ca.yorku.eecs.Strategy.httpBundle;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.neo4j.driver.v1.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class baconToAll implements RESTStrategy {
    //TODO implement this
    @Override
    public httpBundle processRequest(HttpExchange exchange, Driver driver) throws IOException, JSONException {

        List<Record> filteredMovies = null;


        //Attempt to assign Keven Bacon to all nodes
        try (Session session = driver.session()) {

            List<Record> listMovies = null;

            try (Transaction tx1 = session.beginTransaction()) {
                //Check if the actor exists
                StatementResult actorExists = tx1.run(
                        "MATCH (n:actor {id: \"nm0000102\"})\n" +
                                "RETURN n"
                );
                if(actorExists.list().isEmpty()){
                    throw new ClassNotFoundException();
                }

                //Get all movies
                StatementResult movies = tx1.run(
                        "MATCH (m:movie)\n" +
                                "RETURN m"
                );

                listMovies = movies.list();
                if(listMovies.isEmpty()){
                    throw new ClassNotFoundException();
                }
            } catch (ClassNotFoundException e) {
                return new httpBundle(exchange, "NOT FOUND",404);
            }

            //Get the list of movies that bacon has already acted in.
            List<Record> listFilter = null;
            try(Transaction tx2 = session.beginTransaction()){
                StatementResult results = tx2.run(
                        "MATCH (n:actor {id: \"nm0000102\"})-[r]->(m)\n" +
                                "RETURN n,r,m"
                );
                listFilter = results.list();
            }catch(Exception e){
                throw new RuntimeException(e);
            }

            // Extract the movie IDs that Bacon has acted in
            List<String> baconActedInMovieIds = new ArrayList<>();
            for (Record mv : listFilter) {
                baconActedInMovieIds.add(mv.get(2).get("id").asString());
            }

            // Filter out movies that Bacon has already acted in
            filteredMovies = new ArrayList<>();
            for (Record movie : listMovies) {
                String idMovie = movie.get(0).get("id").asString();
                if (!baconActedInMovieIds.contains(idMovie)) {
                    filteredMovies.add(movie);
                }
            }




        } //Make a new session.

        // Now assign Bacon to the new list of movies
        for (Record movie : filteredMovies) {
            String idMovie = movie.get(0).get("id").asString();

            try (Session session = driver.session()){ //Need to call a new session each time you want to write. STUPID STUPID STUPID STUPID STUPID AHHHHHHHHHHHHHHHH

                try {
                    session.writeTransaction(tx3 -> tx3.run(
                            "MATCH(a:actor),(m:movie)\n" +
                                    "WHERE a.id = \"nm0000102\" AND m.id = \"" + idMovie + "\"\n" +
                                    "CREATE (a)-[r:ACTED_IN]->(m)\n"
                    ));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

        }


        return new httpBundle(exchange, "OK", 200);
    }
}
