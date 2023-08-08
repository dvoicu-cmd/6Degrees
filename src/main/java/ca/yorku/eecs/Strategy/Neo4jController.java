package ca.yorku.eecs.Strategy;
import static org.neo4j.driver.v1.Values.parameters;

import ca.yorku.eecs.Strategy.PUT.addActor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.json.*;

import java.net.URI;
import java.util.ArrayList;

public class Neo4jController {
    private Driver driver;
    private String uriDb;
    private ArrayList<RESTStrategy> methods;

    /**
     * Construct a new instance to the neo4j database.
     */
    public Neo4jController(){
        uriDb = "bolt://localhost:7687";
        Config config = Config.builder().withoutEncryption().build(); //no encryption configuration.
        driver = GraphDatabase.driver(uriDb, AuthTokens.basic("neo4j","12345678"),config);
    }


    public void loadGETMethods(){

    }

    public void loadPUTMethods(){

    }

    public void clearMethods(){
        methods.clear();
    }

    /**
     * Closes the instance to the database.
     */
    public void close() {
        driver.close();
    }

    /**
     * Helper method that extracts the string containing the desired endpoint request.
     * @param exchange
     * @return
     */
    private String processURI(HttpExchange exchange) {
        //1st extract the request
        URI uri = exchange.getRequestURI();
        String uriString = uri.toString();
        //2nd get the substring of the uri
        String uriSubString = uriString.substring(8);
        return uriSubString;
    }


}
