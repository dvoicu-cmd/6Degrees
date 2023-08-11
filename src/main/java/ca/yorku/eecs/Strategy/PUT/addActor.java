package ca.yorku.eecs.Strategy.PUT;

import ca.yorku.eecs.Strategy.RESTStrategy;
import ca.yorku.eecs.Strategy.httpBundle;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.v1.*;
import org.json.*;
import ca.yorku.eecs.Utils;

import java.io.*;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.Map;

import static org.neo4j.driver.v1.Values.parameters;

public class addActor implements RESTStrategy {
    //TODO implement this
    @Override
    public httpBundle processRequest(HttpExchange exchange, Driver driver) throws IOException, JSONException {
        JSONObject json;
        String requestBody = Utils.getBody(exchange);
        json = new JSONObject(requestBody);

        /*
        I expect the following format:
            {
            "name": "nameOfActor",
            "actorId": "nm1001213"
            }
        If I don't have this, throw IOException
        */
        if (!(json.has("name")) || !(json.has("actorId"))) {
            throw new IOException("Bad Request");
        }

        //If we get here, we are good to go for DB commands
        String name = (String) json.get("name");
        String id = (String) json.get("actorId");

        try (Session session = driver.session()) {
            try (Transaction tx1 = session.beginTransaction()) {
                StatementResult node_exists = tx1.run(
                        "MATCH (a:Actor {actorId:\"" + id + "\"})\n" +
                                "WITH COUNT(a) > 0 as node_exists\n" +
                                "RETURN node_exists"
                );

                Record r = node_exists.list().get(0);
                if (r.get("node_exists").asBoolean()) {
                    throw new IOException("Bad Request");
                }
            }
            session.writeTransaction(tx2 -> tx2.run(
                    "CREATE (a:Actor {name:\"" + name + "\",actorId:\"" + id + "\"})"
            ));
            session.close();
        }
        return new httpBundle(exchange, "OK", 200);
    }
}
