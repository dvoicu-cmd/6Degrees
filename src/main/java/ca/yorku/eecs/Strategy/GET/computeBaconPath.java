package ca.yorku.eecs.Strategy.GET;

import ca.yorku.eecs.Strategy.RESTStrategy;
import ca.yorku.eecs.Strategy.httpBundle;
import ca.yorku.eecs.Utils;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;

import javax.management.InstanceNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class computeBaconPath implements RESTStrategy {
    //TODO implement this
    @Override
    public httpBundle processRequest(HttpExchange exchange, Driver driver) throws IOException, JSONException {

        httpBundle output = null;

        try (Session session = driver.session()) {
            String s = Utils.processDataInURI(exchange);
            JSONObject json = new JSONObject(s);

            //If the URI is not an actorId, throw err
            if (!json.has("actorId")) {
                throw new IOException("Bad Request");
            }

            //else try to grab the id given
            String id = (String) json.get("actorId");
            try (Transaction tx = session.beginTransaction()) {
                StatementResult exists = tx.run(
                        "MATCH (e:Actor {actorId: \"" + id + "\"})\n" +
                                "RETURN e"
                );

                List<Record> record = exists.list();
                if (record.isEmpty()) {
                    throw new InstanceNotFoundException();
                }

                StatementResult path = tx.run(
                        "MATCH p=shortestPath((startNode)-[*]-(endNode))\n" +
                                "WHERE startNode.actorId = \""+id+"\" AND endNode.actorId = \"nm0000102\"\n" +
                                "RETURN p"
                );

                //Check if this path is valid
                List<Record> records = path.list();
                if(records.isEmpty()){
                    throw new InstanceNotFoundException();
                }


                //Access the specific path.
                Path baconPath = records.get(0).values().get(0).asPath(); //This is stupid


                List<String> pathIds = new ArrayList<>();

                //Get the ids of the nodes in the path.
                for (Node node : baconPath.nodes()) {
                    // Access node properties
                    if(node.containsKey("actorId")){
                        pathIds.add(node.get("actorId").asString());
                    }
                    else if(node.containsKey("movieId")){
                        pathIds.add(node.get("movieId").asString());
                    }
                }

                //Construct jsonOutput
                JSONObject jsonOutput = new JSONObject();
                jsonOutput.put("baconPath",pathIds);

                //Output
                output = new httpBundle(exchange, jsonOutput.toString(), 200);


            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (InstanceNotFoundException e) {
                output = new httpBundle(exchange, "NOT FOUND", 404);
            }
        }
        return output;
    }
}
