package ca.yorku.eecs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import com.sun.net.httpserver.HttpExchange;

public class Utils {
    // use for extracting query params
    public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    // one possible option for extracting JSON body as String
    public static String convert(InputStream inputStream) throws IOException {
                
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    // another option for extracting JSON body as String
    public static String getBody(HttpExchange he) throws IOException {
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            
            int b;
            StringBuilder buf = new StringBuilder();
            while ((b = br.read()) != -1) {
                buf.append((char) b);
            }

            br.close();
            isr.close();
	    
        return buf.toString();
        }

    /**
     * Helper method that extracts the string containing the desired endpoint request.
     * @param exchange
     * @return
     */
    public static String processURI(HttpExchange exchange) throws UnsupportedEncodingException {
        //1st extract the request
        URI uri = exchange.getRequestURI();
        String uriString = uri.toString();
        //2nd get the substring of the uri
        String uriSubString = uriString.substring(8);
        //3rd clear the rest of the characters to the right of the string. (if there is params in the uri)
        if(uriSubString.contains("?")) {
            int afterQuery = uriSubString.indexOf("?");
            uriSubString = uriSubString.substring(0, afterQuery);
        }
        return uriSubString;
    }

    public static String processDataInURI(HttpExchange exchange) throws UnsupportedEncodingException {
        //1st extract the request
        URI uri = exchange.getRequestURI();
        String uriString = uri.toString();
        //3rd clear the rest of the characters to the right of the string. (if there is params in the uri)
        if(uriString.contains("?")) {
            int afterQuery = uriString.indexOf("?");
            uriString = uriString.substring(afterQuery+1);
            uriString = "{"+uriString+"}";
        }
        else{
            throw new UnsupportedEncodingException();
        }
        return uriString;
    }

    public static String stripBraces(String s){
        String output;
        output = s.substring(1,s.length()-1);
        return output;
    }



        /*
        This implementation sucks. Just sucks.
        I could not for the life of me figure out a way to parse the jumbled data from exchange.getRequestBody into JSON
        So I just said: To hell with it, I'm parsing the data by hand using regex.

        August 10th Dan: Turns out, postman is the reason why I had to develop this. Regular requests in .robot files are easy to parse.
         */
    public static Map<String,String> getMapBody(HttpExchange exchange) throws IOException {

        /*
        Don't ask. I don't know ether
         */

        String body = Utils.getBody(exchange);

        Map<String,String> map = new HashMap<>();

        Pattern newEntry = Pattern.compile("----------------------------");
        Matcher newEntryMatcher = newEntry.matcher(body);

        newEntryMatcher.find();
        while(!(newEntryMatcher.hitEnd())){
            try {
                int startOfEntry = newEntryMatcher.end();
                newEntryMatcher.find();
                int endOfEntry = newEntryMatcher.start();
                String entry = body.substring(startOfEntry, endOfEntry);
                putKeyValue(entry, map);
            }
            catch(Exception e){
                break; //This is stupid.
            }
        }
        return map;
    }

    private static void putKeyValue(String subString, Map<String, String> map){
        //Build matcher for ending commas:
        Pattern endComma = Pattern.compile("\"");
        Matcher endCommaMatcher = endComma.matcher(subString);


        //First get indexes of the key
        Pattern nameEqu = Pattern.compile("name=\"");
        Matcher keyStartMatcher = nameEqu.matcher(subString);

        keyStartMatcher.find();
        int keyStart = keyStartMatcher.end();

        endCommaMatcher.find(keyStart+1);
        int keyEnd = endCommaMatcher.end();

        String key = subString.substring(keyStart+1, keyEnd-1);

        if(key.contains("}") || key.contains("{") || key.equals("")) return; //Edge case


        //Now extract the indexes of the value
        Pattern newLine = Pattern.compile("\n\"");
        Matcher matchNewLine = newLine.matcher(subString);

        matchNewLine.find();
        int valueStart = matchNewLine.end();

        endCommaMatcher.find(valueStart);
        int valueEnd = endCommaMatcher.end();

        String value = subString.substring(valueStart,valueEnd-1);

        map.put(key,value);

    }
}