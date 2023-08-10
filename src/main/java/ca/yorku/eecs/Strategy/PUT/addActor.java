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


        Map<String,String> s = Utils.getMapBody(exchange);
        JSONObject json = new JSONObject(s);

        /*
        I expect the following format:
            {
            "name": "nameOfActor",
            "actorId": "nm1001213"
            }
        If I don't have this, throw IOException
        */
        if( !(json.has("name")) || !(json.has("actorId")) ){
            throw new IOException("Bad Request");
        }



        try (Session session = driver.session()){
            try(Transaction tx = session.beginTransaction()){
                StatementResult node_exists = tx.run(
                        "MATCH (a:Actor {actorId:\""+s.get("actorId")+"\"})\n" +
                                "WITH COUNT(a) > 0 as node_exists\n" +
                                "RETURN node_exists"
                );

                Record r = node_exists.list().get(0);
                if(r.get("node_exists").asBoolean()){
                    throw new IOException("Bad Request");
                }
            }

            session.writeTransaction(tx -> tx.run(
                    "CREATE (a:Actor {name:\""+s.get("name")+"\",actorId:\""+s.get("actorId")+"\"})"
            ));
            session.close();
        }


        return new httpBundle(exchange, s.toString(), 200);
    }
}
