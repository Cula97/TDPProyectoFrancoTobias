package francotobias.tdpproyecto;

import android.location.Location;
import java.util.LinkedList;

public class RouteNumber {
    protected LinkedList<Bus> fleet;
    protected Route route;
    protected LinkedList<Location> stops;
    protected String routeID;
    protected String company;


    public RouteNumber(String ID,  Route r) {
        routeID = ID;
        route = r;
    }

    public RouteNumber(String ID,  Route r, LinkedList<Bus> f, LinkedList<Location> s) {
        routeID = ID;
        route = r;
        fleet = f;
        stops = s;
    }



    public void addBus(Bus b) {
        fleet.add(b);
    }

    public void removeBus(Bus b) {
        fleet.remove(b);
    }

    public void addStop(Location l) {
        stops.add(l);
    }

    public void removeStop(Location l) {
        stops.remove(l);
    }

}
