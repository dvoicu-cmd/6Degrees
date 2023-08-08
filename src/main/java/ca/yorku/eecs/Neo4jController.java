package ca.yorku.eecs;
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

public class Neo4jController {
    private Driver driver;
    private String uriDb;

    /**
     * Construct a new instance to the neo4j database.
     */
    public Neo4jController(){
        uriDb = "bolt://localhost:7687";
        Config config = Config.builder().withoutEncryption().build(); //no encryption configuration.
        driver = GraphDatabase.driver(uriDb, AuthTokens.basic("neo4j","12345678"),config);
    }

    /**
     * Closes the instance to the database.
     */
    public void close() {
        driver.close();
    }

    /*
    PUT METHODS
     */

    public void addActor(HttpExchange exchange){

    }

    public void addMovie(HttpExchange exchange) {
    }

    public void addRelationship(HttpExchange exchange) {
    }

    /*
    GET METHODS
     */



}
