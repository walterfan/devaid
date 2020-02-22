package com.github.walterfan.devaid;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.net.URI;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	String q= "0.8";
    	double num = Double.parseDouble(q);
    	String q2= "0.7";
    	double num2 = Double.parseDouble(q2);
    	
    	
    	System.out.println(num + ", " + num2);
    	System.out.println(num > num2);
        assertTrue( true );
    }
    
    public void testUriMethods() {
    	try {
			URI aUri = new URI("https://www.github.com/api/v1");
			String newUriStr = aUri.toString() + "/calls/id";
			URI cUri = new URI(newUriStr);
			System.out.println(cUri.toString());
		} catch (Exception e) {
			fail(e.getMessage());
		}
    }
    
}
