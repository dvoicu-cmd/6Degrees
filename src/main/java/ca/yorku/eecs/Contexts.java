package ca.yorku.eecs;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

/**
 * Establish an intermediary connection between the live server on App.java and the DBMS in Neo4JController.java
 */
public class Contexts {

    /**
     * pointer to the server.
     */
    private HttpServer server;

    /**
     * pointer to the neo4j database.
     */
    private Neo4jController db;

    /**
     * constructor
     * @param serverPtr pointer to the java http server
     */
    public Contexts(HttpServer serverPtr){
        this.server = serverPtr;
        db = new Neo4jController();
    }

    /**
     * build the endpoints to the server.
     */
    public void assignContexts(){
        server.createContext("/api/v1/addActor",db.addActor());
    }

}
