/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.overlord.rtgov.quickstarts.demos.orders;

import org.switchyard.component.test.mixins.http.HTTPMixIn;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.http.HttpInvoker;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

/**
 * This class provides the client for sending SOAP messages
 * to the Orders switchyard application.
 *
 */
public class OrdersClient {

    private static final String OPERATION = "submitOrder";
    private static final String URL = "/demo-orders/OrderService";
    private static final String XML_PATH = "src/test/resources/xml/";
    
    private static final QName SERVICE = new QName(
            "urn:switchyard-quickstart-demo:orders:0.1.0",
            "OrderService");
    private static final String REMOTE_INVOKER_URL = "http://localhost:8080/switchyard-remote";

    /**
     * Private no-args constructor.
     */
    private OrdersClient() {
    }

    /**
     * Main method for Orders client.
     * 
     * @param args The arguments
     * @throws Exception Failed to send SOAP message
     */
    public static void main(final String[] args) throws Exception {

        if (args.length < 2 || args.length > 3) {
            System.err.println("Usage: OrderClient host:port requestId [count]");
            System.exit(1);
        }
        
        if (args[1].endsWith("resubmit")) {
            OrdersClient.resubmit(args);
        } else {
            OrdersClient.send(args);
        }
        
    }
    
    protected static void send(String[] args) {
        HTTPMixIn soapMixIn = new HTTPMixIn();
        soapMixIn.initialize();

        try {
            String url=args[0]+URL;
            String request=XML_PATH+args[1]+".xml";
            int count=1;
            
            if (args.length == 3 && args[2] != null && args[2].trim().length() > 0) {
                count = Integer.parseInt(args[2]);
            }

            byte[] encoded = new byte[0];
            try {
                encoded = Files.readAllBytes(Paths.get(request));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String t =  new String(encoded, Charset.defaultCharset());

            for (int i=0; i < count; i++) {
                String sender = t;
                String dooo = sender.replace("#ID", "" + randInt(10000, 99999));
                String result = soapMixIn.postString(url, dooo);
            
                System.out.println("Reply "+(i+1)+":\n" + result);
            }
        } finally {
            soapMixIn.uninitialize();
        }
    }
    
    protected static void resubmit(String[] args) {
        String request="/xml/"+args[1]+".xml";
        
        try {
            java.io.InputStream is=OrdersClient.class.getResourceAsStream(request);
            byte[] encoded = Files.readAllBytes(Paths.get(request));
            String t =  new String(encoded, Charset.defaultCharset());
            t.replaceAll("-#ID", ""+randInt(10000,99999));
            DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
            org.w3c.dom.Document doc=builder.parse(t);
            
            is.close();
            
            Object content=new DOMSource(doc.getDocumentElement());


            // Create a new remote client invoker
            RemoteInvoker invoker = new HttpInvoker(REMOTE_INVOKER_URL);
    
            // Create the request message
            RemoteMessage message = new RemoteMessage();
            message.setService(SERVICE).setOperation(OPERATION).setContent(content);
    
            // Invoke the service
            RemoteMessage reply = invoker.invoke(message);
            if (reply.isFault()) {
                System.err.println("Oops ... something bad happened.  "
                        + reply.getContent());
                if (reply.getContent() instanceof Exception) {
                    ((Exception)reply.getContent()).printStackTrace();
                }
            } else {
                System.out.println("Response: "+reply.getContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
