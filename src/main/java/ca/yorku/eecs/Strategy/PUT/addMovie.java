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

public class addMovie implements RESTStrategy {
    //TODO implement this
    @Override
    public httpBundle processRequest(HttpExchange exchange, Driver driver) {

        //Map<String,String> s = Utils.getMapBody(exchange);


        return null;
    }
}
