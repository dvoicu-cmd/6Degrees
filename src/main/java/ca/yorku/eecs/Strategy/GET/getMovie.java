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
import java.util.ArrayList;
import java.util.List;

public class getMovie implements RESTStrategy {
    //TODO implement this
    public httpBundle processRequest(HttpExchange exchange, Driver driver) {

        httpBundle output = null;

        try (Session session = driver.session()) {
            String s = Utils.processDataInURI(exchange);
            JSONObject json = new JSONObject(s);

            //If the URI is not a movieId, throw err
            if(!json.has("movieId")){
                throw new IOException("Bad Request");
            }

            //else try to grab the id given
            String id = (String) json.get("movieId");
            try (Transaction tx = session.beginTransaction()) {
                StatementResult exists = tx.run(
                        "MATCH (e:Movie {movieId: \""+id+"\"})\n" +
                                "RETURN e"
                );

                //If the query turns out blank, throw instance not found exception.
                List<Record> record = exists.list();
                if(record.isEmpty()){
                    throw new InstanceNotFoundException();
                }

                //If not thrown, lets continue flow and get the movie node.
                Value movieNode = record.get(0).get(0); //Every movie is unique, there should not be more movies in the query.

                StatementResult results = tx.run(
                        "MATCH (n:Movie {movieId: \""+id+"\"})-[r]-(m)\n" +
                                "RETURN n,r,m"
                );

                //Find all possible relationships
                List<List<Value>> foundRelations = new ArrayList<>(); //An n sized list of size 3 lists
                while(results.hasNext()){
                    foundRelations.add(results.next().values());
                }

                List<Value> nodesOfActors = new ArrayList<>();
                for (List<Value> foundRelation : foundRelations) {
                    nodesOfActors.add(foundRelation.get(2));
                }

                List<String> actorIds = new ArrayList<>();
                for (Value actorNode : nodesOfActors) {
                    actorIds.add(actorNode.get("actorId").asString());
                }

                //Now construct the output
                JSONObject jsonOutput = new JSONObject();
                jsonOutput.put("actors",actorIds);
                jsonOutput.put("name",movieNode.get("name").asString());
                jsonOutput.put("actorId",movieNode.get("movieId").asString());

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
