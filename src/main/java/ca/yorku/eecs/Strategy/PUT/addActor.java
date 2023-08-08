package ca.yorku.eecs.Strategy.PUT;

import ca.yorku.eecs.Strategy.RESTStrategy;
import ca.yorku.eecs.Strategy.httpBundle;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.json.*;

import java.io.*;

import static org.neo4j.driver.v1.Values.parameters;

public class addActor implements RESTStrategy {
    //TODO implement this
    @Override
    public httpBundle processRequest(HttpExchange exchange, Driver driver) throws IOException, JSONException {


        InputStream requestBody = exchange.getRequestBody();
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(requestBody, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);

        JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());



        try (Session session = driver.session()){
            session.writeTransaction(tx -> tx.run("MERGE (a:Author {author: $x})",
                    parameters("x", "I got here")));
            session.close();
        }


        return new httpBundle(exchange, "OK", 200);
    }
}
