package ca.yorku.eecs.Strategy.GET;

import ca.yorku.eecs.Strategy.RESTStrategy;
import ca.yorku.eecs.Strategy.httpBundle;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.v1.Driver;

public class computeBaconPath implements RESTStrategy {
    //TODO implement this
    @Override
    public httpBundle processRequest(HttpExchange exchange, Driver driver) {
        return null;
    }
}
