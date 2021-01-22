package com.example.walksapp;

import android.location.Location;
import java.util.ArrayList;

/**
 * Manages distance calculation
 */
public class DistanceCalculator {

    /**
     * Calculates total distance of route
     * @param route
     * @param sectionList
     * @return
     */
    public double calculateDistance(Route route, ArrayList<Section> sectionList) {

        // Initialises total distance as 0
        double distance = 0;

        // Iterates through sections that comprise route
        for (Section section : sectionList) {

            // Creates new Location object based upon current section's coordinates
            Location currentLatLng = new Location("");
            currentLatLng.setLatitude(Double.parseDouble(section.getSectionLat()));
            currentLatLng.setLongitude(Double.parseDouble(section.getSectionLng()));

            // Checks whether current section is first section
            if (section.equals(sectionList.get(0))) {

                // Creates new Location object based upon route's starting coordinates
                Location start = new Location("");
                start.setLatitude(Double.parseDouble(route.getStartLat()));
                start.setLongitude(Double.parseDouble(route.getStartLng()));

                // Adds distance between start of route and first section to total distance
                distance += start.distanceTo(currentLatLng);

            // Loop proceeds to sections that are not first section
            } else {

                // Ensures that accessed section is never out of bounds
                if (sectionList.indexOf(section) < (sectionList.size())) {

                    // Accesses previous section
                    Section previousSection = sectionList.get(sectionList.indexOf(section) - 1);

                    // Creates new Location object from previous section's coordinates
                    Location previousLatLng = new Location("");
                    previousLatLng.setLatitude(Double.parseDouble(previousSection.getSectionLat()));
                    previousLatLng.setLongitude(Double.parseDouble(previousSection.getSectionLng()));

                    // Adds to total distance that between current section and previous section
                    distance += currentLatLng.distanceTo(previousLatLng);

                    // Checks whether current section is final section
                    if (section.equals(sectionList.get(sectionList.size() - 1))) {

                        // Creates new Location object from route's ending coordinates
                        Location end = new Location("");
                        end.setLatitude(Double.parseDouble(route.getEndLat()));
                        end.setLongitude(Double.parseDouble(route.getEndLng()));

                        // Adds to total distance that between final section and end of route
                        distance += currentLatLng.distanceTo(end);
                    }
                }
            }
        }

        return distance;
    }

    /**
     * Converts distance from metres to kilometres
     * @param distance
     * @return
     */
    public double convertToKm(double distance) {

        double distanceInKm = distance / 1000;

        return distanceInKm;
    }
}
