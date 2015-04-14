package com.craig.carpark2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class Tasks {
	
	
	public static String getStringFromInputStream(InputStream is) {
		 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}
	
	public static List<CarParkModel> makeFakeData(){
		
		List<CarParkModel> CarParks = new ArrayList<CarParkModel>();
		
		CarParkModel cp1 = new CarParkModel();
		cp1.setId(0);
		cp1.setName("SECC");
		cp1.setSpaces(55);
		cp1.setStatus("Spaces");
		CarParks.add(cp1);
		
		CarParkModel cp2 = new CarParkModel();
		cp2.setId(1);
		cp2.setName("Shields Road");
		cp2.setSpaces(11);
		cp2.setStatus("Spaces");
		CarParks.add(cp2);
		
		CarParkModel cp3 = new CarParkModel();
		cp3.setId(2);
		cp3.setName("Duke Street");
		cp3.setSpaces(0);
		cp3.setStatus("Full");
		CarParks.add(cp3);
		
		CarParkModel cp4 = new CarParkModel();
		cp4.setId(4);
		cp4.setName("Cadogan Square");
		cp4.setSpaces(8);
		cp4.setStatus("Spaces");
		CarParks.add(cp4);
		
		CarParkModel cp5 = new CarParkModel();
		cp5.setId(5);
		cp5.setName("Charing Cross");
		cp5.setSpaces(0);
		cp5.setStatus("Closed");
		CarParks.add(cp5);
		
		CarParkModel cp6 = new CarParkModel();
		cp6.setId(6);
		cp6.setName("High Street");
		cp6.setSpaces(65);
		cp6.setStatus("Spaces");
		CarParks.add(cp6);
		
		
		
		return CarParks;
		
	}
	
	public static List<CarParkModel> parseData(String dataToParse)
	{
		System.out.println("Starting parse");
		List<CarParkModel> cps = new ArrayList<CarParkModel>();
		
		String name = "";
		String status = "";
		int totalSpaces = 0;
		int spacesTaken = 0;
		int spacesFree = 0;
		
		try
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput( new StringReader ( dataToParse ) );
			int eventType = xpp.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) 
			{ 
					if(eventType == XmlPullParser.START_TAG) 
					{
						int counter = 1;
						
						CarParkModel c = new CarParkModel();

						if (xpp.getName().equalsIgnoreCase("carParkIdentity"))
	            		{
	            			String temp = xpp.nextText();
	            			Log.e("MyTag","Name is " + temp);
	            			String[] split = temp.split(":");
	            			name = split[0];
	            		}					
						else						
							if (xpp.getName().equalsIgnoreCase("carParkStatus"))
							{
								String temp = xpp.nextText();
								Log.e("MyTag","Status is: " + temp);
								if(temp.equals("enoughSpacesAvailable")){
									status = "Spaces!";
								}
								else if(temp.equals("carParkClosed")){
									status = "Closed";	
								} else {
									status = temp;
								}

								
							}
							else							
								if (xpp.getName().equalsIgnoreCase("occupiedSpaces"))
								{ 
									String temp = xpp.nextText();
									Log.e("MyTag","Occupied Spaces: " + temp);
									spacesTaken = Integer.parseInt(temp);
								}
							else							
								if (xpp.getName().equalsIgnoreCase("totalCapacity"))
								{
									String temp = xpp.nextText();
									Log.e("MyTag","Capacity is " + temp);
									Log.e("MyTag","----");
									totalSpaces = Integer.parseInt(temp);
									c.setId(counter);
									c.setName(name);
									spacesFree = totalSpaces - spacesTaken;
									c.setSpaces(spacesFree);
									c.setSpacesTaken(spacesTaken);
									c.setTotalSpaces(totalSpaces);
									c.setStatus(status);
									c.setPercentage(Math.floor(((double) spacesTaken / (double) totalSpaces) * 100));
									cps.add(c);
									System.out.println("added");
									counter++;
									
								}

					}
					
				eventType = xpp.next();
				
			} // End of while
		}
 		catch (XmlPullParserException ae1)
 		{
 			Log.e("MyTag","Parsing error" + ae1.toString());
 		}
 		catch (IOException ae1)
 		{
 			Log.e("MyTag","IO error during parsing");
 		}
		
		Log.e("MyTag","End document");
		
		CarParkModel c = cps.get(1);
		
		System.out.println("first item is: " + c.getName());
		
		return cps;

	}

	public static String getXML(String urlString)throws IOException
    {
	 	String result = "";
    	InputStream anInStream = null;
    	int response = -1;
    	URL url = new URL(urlString);
    	URLConnection conn = url.openConnection();
    	
    	// Check that the connection can be opened
    	if (!(conn instanceof HttpURLConnection))
    			throw new IOException("Not an HTTP connection");
    	try
    	{
    		// Open connection
    		HttpURLConnection httpConn = (HttpURLConnection) conn;
    		httpConn.setAllowUserInteraction(false);
    		httpConn.setInstanceFollowRedirects(true);
    		httpConn.setRequestMethod("GET");
    		httpConn.connect();
    		response = httpConn.getResponseCode();
    		// Check that connection is Ok
    		if (response == HttpURLConnection.HTTP_OK)
    		{
    			// Connection is Ok so open a reader 
    			anInStream = httpConn.getInputStream();
    			InputStreamReader in= new InputStreamReader(anInStream);
    			BufferedReader bin= new BufferedReader(in);
    			
    			// Read in the data from the XML stream
    			bin.readLine(); // Throw away the header
    			String line = new String();
    			while (( (line = bin.readLine())) != null)
    			{
    				result = result + "\n" + line;
    			}
    		}
    	}
    	catch (Exception ex)
    	{
    			throw new IOException("Error connecting");
    	}
    	
    	// Return result as a string for further processing
    	return result;
    	
    } // End of sourceListingString
	
}
