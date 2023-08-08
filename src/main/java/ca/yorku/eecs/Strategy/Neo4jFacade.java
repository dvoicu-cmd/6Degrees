package ca.yorku.eecs.Strategy;
import static org.neo4j.driver.v1.Values.parameters;

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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

import ca.yorku.eecs.Strategy.GET.*;
import ca.yorku.eecs.Strategy.PUT.*;
import ca.yorku.eecs.Utils;

public class Neo4jFacade {
    private Driver driver;
    private String uriDb;
    private ArrayList<RESTStrategy> methods;

    /**
     * Construct a new instance to the neo4j database.
     */
    public Neo4jFacade(){
        uriDb = "bolt://localhost:7687";
        Config config = Config.builder().withoutEncryption().build(); //no encryption configuration.
        driver = GraphDatabase.driver(uriDb, AuthTokens.basic("neo4j","12345678"),config);
        methods = new ArrayList<>();
    }


    private void loadGETMethods(){
        methods.add(new computeBaconNumber());
        methods.add(new computeBaconPath());
        methods.add(new getActor());
        methods.add(new getMovie());
        methods.add(new hasRelationship());
    }

    private void loadPUTMethods(){
        methods.add(new addActor());
        methods.add(new addMovie());
        methods.add(new addRelationship());
    }

    private void clearMethods(){
        methods.clear();
    }

    /**
     * Closes the instance to the database.
     */
    public void close() {
        driver.close();
    }

    //This method follows these steps:
    //1) figure out what the request is
    //2) execute request
    //3) build back the response
    public httpBundle processRequest(HttpExchange exchange){
        String method = exchange.getRequestMethod();

        //declare the output bundle
        httpBundle output = null;

        try {
            switch (method) {
                case "PUT":
                    loadPUTMethods();
                    String uriPUT = processURI(exchange);
                    int PUTid = -1;
                    switch (uriPUT) {
                        case "addActor":
                            PUTid = 0;
                            break;
                        case "addMovie":
                            PUTid = 1;
                            break;
                        case "addRelationship":
                            PUTid = 2;
                            break;
                    }
                    output = methods.get(PUTid).processRequest(exchange, driver); //id correlates to the method we will use.
                    break;
                case "GET":
                    loadGETMethods();
                    String uriGET = processURI(exchange);
                    int GETid = -1;
                    switch (uriGET) {
                        case "computeBaconNumber":
                            GETid = 0;
                            break;
                        case "computeBaconPath":
                            GETid = 1;
                            break;
                        case "getActor":
                            GETid = 2;
                            break;
                        case "getMovie":
                            GETid = 3;
                            break;
                        case "hasRelationship":
                            GETid = 4;
                            break;
                    }
                    output = methods.get(GETid).processRequest(exchange, driver);
                    break;
            }
        } catch(IndexOutOfBoundsException | JSONException | IOException e) {
            e.printStackTrace();
            output = new httpBundle(exchange, "BAD REQUEST", 400);
        }
        clearMethods();
        return output;

    }

    /**
     * Helper method that extracts the string containing the desired endpoint request.
     * @param exchange
     * @return
     */
    private String processURI(HttpExchange exchange) throws UnsupportedEncodingException {
        //1st extract the request
        URI uri = exchange.getRequestURI();
        String uriString = uri.toString();
        //2nd get the substring of the uri

        String uriSubString = uriString.substring(8);
        int afterQuery = uriSubString.indexOf("?");
        uriSubString = uriSubString.substring(0,afterQuery);

        return uriSubString;
    }


}
