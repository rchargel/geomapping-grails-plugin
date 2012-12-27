Geomapping Plugin for Grails
============================
***
Description
-----------

The geomapping plugin for grails provides both a geomappingService and a tag library to your application to simplify the development of applications using google maps api.

geomappingService
-----------------
The Geomapping Service provides an API for geocoding and reverse geocoding.  It also provides an API for querying the location of your users by their IP Address.

### buildBoundingBox
Find a bounding box encompassing a 2.5 kilometer radius around the empire state building.

    def nyLat = 40.7482845
    def nyLng = -73.9855692
    def radiusKilometers = 2.5
    def bb = geomappingService.buildBoundingBox(nyLat, nyLng, radiusKilometers)
    assertTrue(bb.containsCoordinate(40.760439, -73.979336))

### calculateDistance
    // calculates the distance in kilometers between two points
    // New York : 40.714353, -74.005974
    // Johannesburg : -26.204074, 28.047292
    def nyLat = 40.714353
    def nyLng = -74.005974
    def jbLat = -26.204074
    def jbLng = 28.047292
    def distance = geomappingService.calculateDistance(nyLat, nyLng, jbLat, jbLng)
    assertEquals(12840, distance, 0.5)

### getLocationOfIP
   // calculates