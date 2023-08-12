package ca.yorku.eecs.Strategy.GET;

import ca.yorku.eecs.Strategy.RESTStrategy;
import ca.yorku.eecs.Strategy.httpBundle;
import ca.yorku.eecs.Utils;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.*;

import javax.management.InstanceNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class hasRelationship implements RESTStrategy {
    //TODO implement this
    @Override
    public httpBundle processRequest(HttpExchange exchange, Driver driver) {

        //The output
        httpBundle output = null;

        try (Session session = driver.session()) {
            String s = Utils.processDataInURI(exchange);
            s = Utils.stripBraces(s);
            Map<String,String> map = Utils.splitQuery(s);
            JSONObject json = new JSONObject(map);

            //Determine if the movieId and actorId exist in the json input
            if(!json.has("movieId") || !json.has("actorId")){
                throw new IOException("Bad Request");
            }

            //If flow continues, call the query to see if the two nodes exist
            String idMovie = (String) json.get("movieId");
            String idActor = (String) json.get("actorId");

            try(Transaction tx = session.beginTransaction()) {

                //Check if the movie exists
                StatementResult movieExists = tx.run(
                        "MATCH (n:Movie {movieId: \""+idMovie+"\"})\n" +
                                "RETURN n"
                );
                if(movieExists.list().isEmpty()){
                    throw new InstanceNotFoundException();
                }

                //Check if the actor exists
                StatementResult actorExists = tx.run(
                        "MATCH (n:Actor {actorId: \""+idActor+"\"})\n" +
                                "RETURN n"
                );
                if(actorExists.list().isEmpty()){
                    throw new InstanceNotFoundException();
                }

                //If those queries pass, then we are good to go on checking if the relationship exists
                //Check if the relationship exists
                StatementResult query = tx.run(
                        "MATCH (m:Movie {movieId: \"" + idMovie + "\"})-[:ACTED_IN]-(a:Actor {actorId: \"" + idActor + "\"})\n" +
                                "RETURN a, m"
                );
                boolean relationExists;
                relationExists = !query.list().isEmpty(); //The relationship exists if the list is not empty

                //Built output
                JSONObject jsonOutput = new JSONObject();
                jsonOutput.put("actorId",idActor);
                jsonOutput.put("movieId",idMovie);
                jsonOutput.put("hasRelationship",relationExists);

                output = new httpBundle(exchange,jsonOutput.toString(),200);

            }

        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        } catch (InstanceNotFoundException e){
            output = new httpBundle(exchange, "NOT FOUND", 404);
        }


        return output;
    }
}
