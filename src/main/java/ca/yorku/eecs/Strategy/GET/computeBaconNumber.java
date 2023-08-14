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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class computeBaconNumber implements RESTStrategy {
    //TODO implement this class.
    @Override
    public httpBundle processRequest(HttpExchange exchange, Driver driver) throws IOException, JSONException {

        /*
        Ight, I know what your thinking: Would it not be better design if I just reused the computeBaconPath.java class.
        And to that, you are right. However... ctrl-c, ctrl-v change the output ha ha ha ronaldinho soccer.
         */

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
                    // Access node properties
                    if(node.hasLabel("movie")){
                        pathIds.add(node.get("id").asString());
                    }
                }

                //Construct jsonOutput
                JSONObject jsonOutput = new JSONObject();
                jsonOutput.put("baconNumber",pathIds.size());

                //Output
                output = new httpBundle(exchange, jsonOutput.toString(), 200);


            } catch (JSONException e) {
                output = new httpBundle(exchange, "BAD REQUEST", 400);
            } catch (ClassNotFoundException e) {
                output = new httpBundle(exchange, "NOT FOUND", 404);
            } catch (ExceptionInInitializerError e){ //Bacon Exception
                JSONObject jsonOutput = new JSONObject();
                jsonOutput.put("baconNumber",0);
                output = new httpBundle(exchange, jsonOutput.toString(),200);
            }
        }
        return output;
    }
}
