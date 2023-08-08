package ca.yorku.eecs.Strategy;

import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.v1.Driver;

public interface RESTStrategy {
    /**
     * processes the selected request
     * @param exchange
     * @param driver
     * @return
     */
    public abstract httpBundle processRequest(HttpExchange exchange, Driver driver); //Return the httpBundle response back from the DB
}
