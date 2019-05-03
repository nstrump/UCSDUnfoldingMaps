package module6;

import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team and Nathan Trump
 *
 */
public class AirportMarker extends CommonMarker {
	public static List<SimpleLinesMarker> routes;
	
	public AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
	
	}
	
	
	public static int M_SIZE = 5;
	
	//member variables rank elevation of airports low to high(airport elevation given as altitude in parser methods)
	public static float ALT_HIGH =8202;
	public static float ALT_MED = 2756;
	public static float ALT_LOW = 0;
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {

		pg.pushStyle();
		
		colorAltMarkers(pg);
		
		pg.ellipse(x, y, 5, 5);
		
		pg.popStyle();
		
	}

	private void colorAltMarkers(PGraphics pg) {
		//this method colors the airport markers according to their place in elevation range
		Float altitude = getAltitude();
		if (altitude >= ALT_HIGH) {
			pg.fill(237, 42, 42);
		}
		if (altitude < ALT_HIGH && altitude > ALT_MED) {
			pg.fill(255, 216, 0);
		}
		if (altitude <= ALT_MED && altitude > ALT_LOW) {
			pg.fill(98, 221, 84);
		}
		if (altitude <= ALT_LOW) {
			pg.fill(0, 93, 255);
		}
	}	

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		 // show rectangle with title
		String info = "Airport: " + getAirport() + ", " + "Airport Code: "+ getCode();
		String geo = "City: " + getCity() + ", " + "Country: " + getCountry();
		
		pg.pushStyle();
		
		pg.fill(255, 255, 255);
		pg.textSize(12);
		pg.rectMode(PConstants.CORNER);
		pg.rect(x, y-M_SIZE-39, Math.max(pg.textWidth(info)+6, pg.textWidth(geo)+ 6), 39);
		pg.fill(0, 0, 0);
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.text(info, x+3, y-M_SIZE-33);
		pg.text(geo, x+3, y-M_SIZE-18);
		
		pg.popStyle();
		// show routes
		
		
	}
	
	
	private String getCode() {
		return getStringProperty("code");
	}
	
	private String getAirport(){
		return getStringProperty("name");
	}
	
	private String getCity() {
		return getStringProperty("city");
	}
	
	private String getCountry(){
		return getStringProperty("country");
	}
	
	private Float getAltitude() {
		return Float.parseFloat(getStringProperty("altitude")) ;
	}
	
	
}
