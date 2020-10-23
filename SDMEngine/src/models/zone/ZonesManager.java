package models.zone;

import models.store.Store;
import models.user.Owner;
import models.user.User;
import resources.schema.jaxbgenerated.SuperDuperMarketDescriptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ZonesManager {

    private final Set<Zone> zoneSet;


    public ZonesManager(){
        zoneSet = new HashSet<>();
    }


    public boolean isZoneExists(String name) {
        System.out.println("called ZoneManager.isZoneExists()");
        Set<String> zoneNames = zoneSet.stream().map(zone -> zone.getZoneName()).collect(Collectors.toSet());
        return zoneNames.contains(name);
    }

    public void createZoneFromSuperDuperMarketDescriptor(SuperDuperMarketDescriptor sdm, Owner founder){
        System.out.println("called ZoneManager.createZoneFromSuperDuperMarketDescriptor()");
        Zone zone = new Zone(sdm, founder);
        zoneSet.add(zone);
        System.out.println("zoneSet is now: " + zoneSet);
    }


    public Zone getZoneByName(String name){
        for (Zone zone: zoneSet){
            if (zone.getZoneName().equals(name))
                return zone;
        }
        return null;
    }

    public String getFounderOfZone(String zoneName) {
        for (Zone zone: zoneSet){
            if (zone.getZoneName().equals(zoneName))
                return zone.getFounder().getName();
        }
        return "";
    }

    public Set<Zone> getZoneSet() {
        return zoneSet;
    }


    //TODO: Maybe make method that returns name of store taking up location
    public boolean isValidLocation(int[] loc) {
        for (Zone zone: zoneSet){
            for (Store store:zone.getStores()){
                if (Arrays.equals(loc,store.getLocation()))
                    return false;
            }
        }
        return true;
    }
}
