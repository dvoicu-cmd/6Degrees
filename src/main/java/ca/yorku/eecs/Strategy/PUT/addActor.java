package ca.yorku.eecs.Strategy.PUT;

import ca.yorku.eecs.Strategy.RESTStrategy;
import ca.yorku.eecs.Strategy.httpBundle;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.json.*;
import ca.yorku.eecs.Utils;
import java.io.*;
import java.util.Map;

import static org.neo4j.driver.v1.Values.parameters;

public class addActor implements RESTStrategy {
    //TODO implement this
    @Override
    public httpBundle processRequest(HttpExchange exchange, Driver driver) throws IOException, JSONException {


        Map<String,String> s = Utils.getMapBody(exchange);





        try (Session session = driver.session()){
            session.writeTransaction(tx -> tx.run("MERGE (a:Author {author: $x})",
                    parameters("x", "I got here")));
            session.close();
        }


        return new httpBundle(exchange, s.toString(), 200);
    }
}
