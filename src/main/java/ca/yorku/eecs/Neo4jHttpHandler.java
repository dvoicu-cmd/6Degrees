package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Establish an intermediary connection between the live server on App.java and the DBMS in Neo4JController.java
 */
public class Neo4jHttpHandler implements HttpHandler {

    /**
     * pointer to the neo4j database.
     */
    private Neo4jController db;

    /**
     * constructor
     */
    public Neo4jHttpHandler(){
        db = new Neo4jController();
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if(exchange.getRequestMethod().equals("PUT")){
                handlePut(exchange);
            }
            else if(exchange.getRequestMethod().equals("GET")){
                handleGet(exchange);
            }
            else {
                sendString(exchange, "Unimplemented Method\n", 501);
            }
        } catch (Exception e){
            e.printStackTrace();
            sendString(exchange, "Server error\n", 500);
        }
    }

    private void handlePut(HttpExchange exchange) {
        //TODO: addActor, addMovie, addRelationship,
        String s = processURI(exchange);

        switch(s) {
            case "addActor":
                db.addActor(exchange);

                break;
            case "addMovie":
                db.addMovie(exchange);

                break;
            case "addRelationship":
                db.addRelationship(exchange);

                break;
        }

        //TODO: find out a way to get a response back from the controller.
        sendString(exchange)

    }

    private void handleGet(HttpExchange exchange) {

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

    /**
     * Sends the response back to the client.
     * @param exchange
     * @param data
     * @param resetCode
     * @throws IOException
     */
    private void sendString(HttpExchange exchange, String data, int resetCode) throws IOException {
        exchange.sendResponseHeaders(resetCode, data.length());
        OutputStream OS = exchange.getResponseBody();
        OS.write(data.getBytes());
        OS.close();
    }
}
