package ca.yorku.eecs;

import java.io.IOException;
import java.io.OutputStream;

import ca.yorku.eecs.Strategy.Neo4jFacade;
import ca.yorku.eecs.Strategy.httpBundle;
import com.sun.net.httpserver.HttpExchange;

/**
 * Establish an intermediary connection between the live server on App.java and the DBMS in Neo4JController.java
 */
public class HttpInitializer implements com.sun.net.httpserver.HttpHandler {

    /**
     * pointer to the neo4j database.
     */
    private Neo4jFacade db;

    /**
     * constructor
     */
    public HttpInitializer(){
        db = new Neo4jFacade();
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
        db = new Neo4jFacade();

        //PROCESS DB instance (Facade)
        httpBundle output = db.processRequest(exchange);

        //CLOSE DB instance
        db.close();
        db = null;
        //Return the built response
        return output;
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
