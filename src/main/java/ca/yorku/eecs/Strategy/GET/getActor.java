package ca.yorku.eecs.Strategy.GET;

import ca.yorku.eecs.Strategy.RESTStrategy;
import ca.yorku.eecs.Strategy.httpBundle;
import ca.yorku.eecs.Utils;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;

import javax.management.InstanceNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class getActor implements RESTStrategy {
    //TODO implement this
    @Override
    public httpBundle processRequest(HttpExchange exchange, Driver driver) {

        httpBundle output = null;

        try (Session session = driver.session()) {
            String s = Utils.processDataInURI(exchange);
            JSONObject json = new JSONObject(s);

            //If the URI is not an actorId, throw err
            if(!json.has("actorId")){
                throw new IOException("Bad Request");
            }

            //else try to grab the id given
            String id = (String) json.get("actorId");
            try (Transaction tx = session.beginTransaction()) {
                StatementResult exists = tx.run(
                  "MATCH (e:Actor {actorId: \""+id+"\"})\n" +
                          "RETURN e"
                );

                List<Record> record = exists.list();

                if(record.isEmpty()){
                    throw new InstanceNotFoundException();
                }

                //If not thrown, lets continue flow and get the actor node.

                Value actorNode = record.get(0).get(0); //Every actor is unique, there should not be more actors in the query.

                StatementResult results = tx.run(
                        "MATCH (n:Actor {actorId: \""+id+"\"})-[r]->(m)\n" +
                                "RETURN n,r,m"
                );

                //Find all possible relationships
                List<List<Value>> foundRelations = new ArrayList<>(); //A list of size 3 lists
                while(results.hasNext()){
                    foundRelations.add(results.next().values());
                }

                List<Value> nodesOfMovies = new ArrayList<>();
                for (int i = 0; i < foundRelations.size(); i++) {
                    nodesOfMovies.add(foundRelations.get(i).get(2));
                }

                List<String> movieIds = new ArrayList<>();
                for (Value movieNode : nodesOfMovies) {
                    movieIds.add(movieNode.get("movieId").asString());
                }

                //Now construct the output
                JSONObject jsonOutput = new JSONObject();
                jsonOutput.put("movies",movieIds);
                jsonOutput.put("name",actorNode.get("name").asString());
                jsonOutput.put("actorId",actorNode.get("actorId").asString());

                output = new httpBundle(exchange, jsonOutput.toString(), 200);

            }
            session.close();
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
        catch (InstanceNotFoundException e){
            output = new httpBundle(exchange, "NOT FOUND", 404);
        }

        return output;
    }
}
