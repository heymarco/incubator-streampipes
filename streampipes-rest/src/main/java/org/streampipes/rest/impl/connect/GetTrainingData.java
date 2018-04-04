package org.streampipes.rest.impl.connect;


import ch.qos.logback.core.net.SyslogOutputStream;
import com.sun.java.browser.plugin2.DOM;
import org.apache.http.client.fluent.Request;
import org.eclipse.rdf4j.query.algebra.Str;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.connect.firstconnector.format.json.JsonFormat;
import org.streampipes.connect.firstconnector.format.json.JsonParser;
import org.streampipes.connect.firstconnector.protocol.Protocol;
import org.streampipes.connect.firstconnector.protocol.set.HttpProtocol;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.model.modelconnect.DomainPropertyProbability;
import org.streampipes.model.modelconnect.DomainPropertyProbabilityList;
import org.streampipes.model.modelconnect.ProtocolDescription;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * This is just a wrapper to find the best solution to extract training data from multiple different sources to train our model repository
 */
public class GetTrainingData {
    public static void main(String... args) {
//        new GetTrainingData().getTrainingData();

        Double[] x = {34292.0, 34292.0, 34292.0, 84155.0, 34466.0, 83352.0, 84503.0, 63916.0, 9456.0, 9456.0, 9456.0, 8359.0, 84371.0, 63743.0, 8280.0, 8280.0, 34454.0, 84364.0, 94081.0, 57334.0};
//        String[] x = {"a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a"};

        new GetTrainingData().getDomainPropertyProbability(x);

    }

    public DomainPropertyProbabilityList getDomainPropertyProbability(Object[] sampleData) {

        String url = "http://localhost/predict";

        String numbers = "";
        for (Object d : sampleData) {
            if (d instanceof String) {
                numbers = numbers + "\"" + d + "\",";
            } else  {
                numbers = numbers + d + ",";
            }

        }
        numbers = numbers.substring(0, numbers.length() - 1) + "";


        try {
            url = url + "?X=[" + URLEncoder.encode(numbers, "UTF-8") + "]";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        DomainPropertyProbabilityList result = new DomainPropertyProbabilityList();
        try {
            url = url.replaceAll("%2C", ",");
            System.out.println(url);
            String s = Request.Get(url)
            .connectTimeout(10000)
            .socketTimeout(10000)
            .execute().returnContent().asString();


            JsonDataFormatDefinition jsonDataFormatDefinition = new JsonDataFormatDefinition();

            s = s.substring(2, s.length());

            while(s.indexOf("[") > -1) {
                String tmpString = s.substring(s.indexOf("["), s.indexOf("]") + 1);
                s = s.substring(s.indexOf("]") + 1, s.length());
                result.addDomainPropertyProbability(parseDomainPropertyProbability(tmpString));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private DomainPropertyProbability parseDomainPropertyProbability(String s) {
        DomainPropertyProbability result = new DomainPropertyProbability();
        String property = s.substring(s.indexOf("\""), s.lastIndexOf("\""));
        String numberString = s.substring(s.indexOf(",") + 1, s.indexOf("]"));
        double number = Double.parseDouble(numberString);

        result.setDomainProperty(property);
        result.setProbability(number);
        return result;
    }

    public void getTrainingData() {
                ProtocolDescription httpDescription = new HttpProtocol().declareModel();


        JsonParser parser = new JsonParser(true, "elements");
        JsonFormat format = new JsonFormat();


        Protocol httpProtocol = new HttpProtocol().getInstance(httpDescription, parser, format);

        List<Map<String, Object>> result =  httpProtocol.getNElements(100);

        String[] tagsLabels = {"addr:city", "addr:country", "addr:housenumber", "addr:postcode", "addr:street", "amenity", "dispensing",
                "email", "fax", "name", "opening_hours", "operator", "phone", "website", "wheelchair"};
        String resultString = "";


        for (String label : tagsLabels) {
            for (int j = 0; j < result.size(); j = j + 20) {
                String subString = "[";
                for (int i = 0; i < 20; i++) {
                    Map<String, Object> tags = (Map<String, Object>) result.get(i + j).get("tags");
                    String s = (String) tags.get(label);
//                    subString = subString + "\"" + s + "\", ";
                    subString = subString + "" + s + ", ";

//                subString = subString + "" + result.get(i).get("lon") + ", ";
                }

                subString = subString.substring(0, subString.length() - 2);
                subString = subString + "]";

                resultString = resultString + subString + ",\n";
            }


            resultString = resultString.substring(0, resultString.length() - 2);
            System.out.println(label);
            try {
                new PrintStream(System.out, true, "UTF-8").println(resultString);
                System.out.println();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            resultString = "";
        }


    }
}
