package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team and Nathan Trump
 *
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	private Marker lastSelected;
	private Marker lastClicked;
	
	public void setup() {
		// setting up PAppler
		size(900,700, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 250, 50, 750, 650);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(5);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			String airID = (String) feature.getProperty("airportID");
			int airInt=Integer.parseInt(airID);
			airports.put(airInt, feature.getLocation());
		
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			//System.out.println(sl.getProperties());
			
			routeList.add(sl);
		}
		
		
		

		map.addMarkers(routeList);
		hideRoutes();
		map.addMarkers(airportList);
		
	}
	
	public void draw() {
		background(100);
		map.draw();
		addKey();
		
	}
	
	private void addKey() {
		
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		rect(xbase, ybase, 200, 250);
		
		fill(0);
		textAlign(CENTER);
		textSize(16);
		text("Elevation Key", xbase+87, ybase+25);
		textAlign(LEFT, CENTER);
		line(xbase, ybase+27, 225, ybase+27);
		fill(237, 42, 42);
		ellipse(xbase+25, ybase+50, 5, 5);
		fill(0);
		textSize(14);
		textAlign(LEFT, CENTER);
		text("Above 8,202 ft.", xbase+35, ybase+50);
		fill(255, 216, 0);
		ellipse(xbase+25, ybase+100, 5, 5);
		fill(0);
		text("8,202 ft. - 2,756 ft.", xbase+35, ybase+100);
		fill(98, 221, 84);
		ellipse(xbase+25, ybase+150, 5, 5);
		fill(0);
		text("2,756 ft. - sea level", xbase+35, ybase+ 150);
		fill(0, 93, 255);
		ellipse(xbase+25, ybase+200, 5, 5);
		fill(0);
		text("At or below sea level", xbase+35, ybase+200);
		
		
		
	}

	public void mouseMoved() {
		
		if (lastSelected!=null) {
			lastSelected.setSelected(false);
			lastSelected=null;
		}
		
		selectMarkerIfHover(airportList);
	}

	private void selectMarkerIfHover(List<Marker> list) {
		
		if (lastSelected!=null) {
			return;
		}
		
		for (Marker m : list){
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map, mouseX, mouseY)) {
				lastSelected=marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	public void mouseClicked() {
		if (lastClicked!=null) {
			hideRoutes();
			lastClicked=null;
		}
		else if (lastClicked==null) {
			checkAirportsForClick();
		}
	}

	private void checkAirportsForClick() {
		if (lastClicked!=null) return;
		for (Marker airM : airportList) {	
			if (airM.isInside(map, mouseX, mouseY)) {
				lastClicked=(CommonMarker)airM;
				for (Marker route : routeList) {
					String src = (String)route.getProperty("source");
					String src2 = (String)lastClicked.getProperty("airportID");
					if(src.equals(src2)) { 
						route.setHidden(false);
					}	
				}
			}
		}
	}

	private void hideRoutes() {
		for (Marker m: routeList) {
			m.setHidden(true);
		}
		
	}
	
	
}
