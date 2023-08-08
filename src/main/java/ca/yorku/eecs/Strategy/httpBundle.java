package ca.yorku.eecs.Strategy;

import javax.xml.ws.spi.http.HttpExchange;
import org.json.*;

/**
 * An intermediary class the contains the important data between the call from the server to the db.
 */
public class httpBundle {

    private HttpExchange exchange;
    private String data;
    private int resetCode;

    public httpBundle(){}

    public httpBundle(HttpExchange exchange, String data, int resetCode){
        this.exchange = exchange;
        this.data = data;
        this.resetCode = resetCode;
    }

    /*
    exchange setter getter
     */
    public void setExchange(HttpExchange exchange){
        this.exchange = exchange;
    }
    public HttpExchange getExchange(){
        return this.exchange;
    }

    /*
    data setter getter
     */
    public void setData(String data){
        this.data = data;
    }
    public String getData(){
        return this.data;
    }

    /*
    resetcode setter getter
     */
    public void setResetCode(int resetCode){
        this.resetCode = resetCode;
    }
    public int getResetCode(){
        return this.resetCode;
    }



}
