package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import ca.yorku.eecs.Strategy.Neo4jController;
import ca.yorku.eecs.Strategy.httpBundle;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

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


    /**
     * Main handler method. the initial exchange goes through here.
     * @param exchange
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            //If the request is a PUT or GET request,
            if(exchange.getRequestMethod().equals("PUT") || exchange.getRequestMethod().equals("GET")){
                httpBundle output = handleStrategy(exchange); //Execute a method that will be figured out later.
                sendString(output.getExchange(), output.getData(), output.getResetCode()); //return the response.
            }
            else {
                sendString(exchange, "Unimplemented Method\n", 501);
            }
        } catch (Exception e){
            e.printStackTrace();
            sendString(exchange, "Server error\n", 500);
        }
    }

    /**
     * Method that bridges over to the neo4jdb controller.
     * @param exchange
     * @return
     */
    public httpBundle handleStrategy(HttpExchange exchange){
        //OPEN DB instance
        db = new Neo4jController();

        //1) figure out what the request is

        //2) execute request

        //3) build back the response

        //CLOSE DB instance
        db.close();
        db = null;
        //Return the built response
        return null;
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
