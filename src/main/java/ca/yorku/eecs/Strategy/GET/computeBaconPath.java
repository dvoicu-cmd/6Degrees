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
                        "MATCH (e:actor {id: \"" + id + "\"})\n" +
                                "RETURN e"
                );

                List<Record> record = exists.list();
                if (record.isEmpty()) {
                    throw new ClassNotFoundException();
                }

                StatementResult path = tx.run(
                        "MATCH p=shortestPath((startNode)-[*]-(endNode))\n" +
                                "WHERE startNode.id = \""+id+"\" AND endNode.id = \"nm0000102\"\n" +
                                "RETURN p"
                );

                //Check if this path is valid
                List<Record> records;
                try{
                    records = path.list();
                }catch(Exception e){ //If you get an error -> Keven Bacon Node
                    throw new ExceptionInInitializerError();
                }

                if(records.isEmpty()){
                    throw new ClassNotFoundException();
                }


                //Access the specific path.
                Path baconPath = records.get(0).values().get(0).asPath(); //This is stupid


                List<String> pathIds = new ArrayList<>();

                //Get the ids of the nodes in the path.
                for (Node node : baconPath.nodes()) {
                        pathIds.add(node.get("id").asString());
                }

                //Construct jsonOutput
                JSONObject jsonOutput = new JSONObject();
                jsonOutput.put("baconPath",pathIds);

                //Output
                output = new httpBundle(exchange, jsonOutput.toString(), 200);


            } catch (JSONException e) {
                output = new httpBundle(exchange, "BAD REQUEST", 400);
            } catch (ClassNotFoundException e) {
                output = new httpBundle(exchange, "NOT FOUND", 404);
            } catch (ExceptionInInitializerError e){ //Bacon Exception
                JSONObject jsonOutput = new JSONObject();
                List<String> arr = new ArrayList<>();
                arr.add("nm0000102");
                jsonOutput.put("baconPath",arr);
                output = new httpBundle(exchange, jsonOutput.toString(),200);
            }
        }
        return output;
    }
}
