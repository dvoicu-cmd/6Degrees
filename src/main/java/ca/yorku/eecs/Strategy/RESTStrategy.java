package ca.yorku.eecs.Strategy;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.neo4j.driver.v1.Driver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface RESTStrategy {
    /**
     * processes the selected request
     * @param exchange
     * @param driver
     * @return
     */
    public abstract httpBundle processRequest(HttpExchange exchange, Driver driver) throws IOException, JSONException; //Return the httpBundle response back from the DB
}
