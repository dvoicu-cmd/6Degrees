package ca.yorku.eecs.Strategy.PUT;

import ca.yorku.eecs.Strategy.RESTStrategy;
import ca.yorku.eecs.Strategy.httpBundle;
import ca.yorku.eecs.Utils;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.*;

import java.io.IOException;

public class addRelationship implements RESTStrategy {
    //TODO implement this
    @Override
    public httpBundle processRequest(HttpExchange exchange, Driver driver) throws IOException, JSONException {
        JSONObject json;
        String requestBody = Utils.getBody(exchange);
        json = new JSONObject(requestBody);

        /*
        I expect the following format:
            {
            "actorId": "nm#",
            "movieId": "nm#"
            }

            or

            {
            "movieId": "nm#",
            "actorId": "nm#"
            }

        If I don't have this, throw IOException
        */
        if (!(json.has("actorId")) || !(json.has("movieId"))) {
            throw new IOException("Bad Request");
        }

        //If we get here, we are good to go for DB commands
        String idActor = (String) json.get("actorId");
        String idMovie = (String) json.get("movieId");

        try (Session session = driver.session()) {
            //First check if this relationship exists
            try (Transaction tx1 = session.beginTransaction()) {
                StatementResult relation_exists = tx1.run(
                        "MATCH (a:Actor {actorId: \""+idActor+"\"})-[:ACTED_IN*1]-(m:Movie {movieId: \""+idMovie+"\"})\n" +
                                "RETURN EXISTS((a)-[:ACTED_IN*1]-(m)) AS bool"
                );

                //If the movie already exists in db, throw exception
                if (relation_exists.hasNext()){
                    throw new IOException("Bad Request");
                }
            }
            //Else, you want to create that relationship
            session.writeTransaction(tx2 -> tx2.run(
                    "MATCH(a:Actor),(m:Movie)\n"+
                            "WHERE a.actorId = \""+idActor+"\" AND m.movieId = \""+idMovie+"\"\n"+
                            "CREATE (a)-[r:ACTED_IN]->(m)\n"
            ));
            session.close();
        }
        return new httpBundle(exchange, "OK", 200);
    }
}
