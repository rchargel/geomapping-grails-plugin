Geomapping Plugin for Grails
============================
* auto-gen TOC:
{:toc}

## Description
The geomapping plugin for grails provides both a geomappingService and a tag library to your application to simplify the development of applications using google maps api.

***
## Configuration
There is really only one configuration parameter for the geomapping plugin

*geomapping.apiKey=YOUR_GOOGLE_API_KEY*

***
## Geomapping Service
The Geomapping Service provides an API for geocoding and reverse geocoding.  It also provides an API for querying the location of your users by their IP Address.

### buildBoundingBox
Creates a bounding box encompassing a specified radius in kilometers around a latitude and longitude.  The bounding box that is created errors on the side of being too large.  The best use of this is to quickly search for items whose coordinates fall within the bounding box, then filter out the rest using the calcuateDistance method.

    def nyLat = 40.7482845
    def nyLng = -73.9855692
    def radiusKilometers = 2.5
    def bb = geomappingService.buildBoundingBox(nyLat, nyLng, radiusKilometers)
    assertTrue(bb.containsCoordinate(40.760439, -73.979336))

### calculateDistance
Calculates the distance in kilometers between two points.

    // New York : 40.714353, -74.005974
    // Johannesburg : -26.204074, 28.047292
    def nyLat = 40.714353
    def nyLng = -74.005974
    def jbLat = -26.204074
    def jbLng = 28.047292
    def distance = geomappingService.calculateDistance(nyLat, nyLng, jbLat, jbLng)
    assertEquals(12840, distance, 0.5)

### getCoordinatesFromIP
Gets the likely coordinates of an IP address.  For most users, this will be their public IP address, and will likely be close enough to their real location that it will provide a good starting point for a map display.  However if the user is being routed through an internal network or proxy, this location may be in another city or even country.
    
    def location = geomappingService.getLocationOfIP(request.getRemoteAddr())
    println "${location.latitude} / ${location.longitude}"

### getLocationsFromCoordinates
Gets a list of locations (including address information) from a set of coordinates.  The locations are sorted in order of most likely to least likely.

    def locations = geomappingService.getLocationsFromCoordinates(38.8976777, -77.0365170)
    locations.each { location ->
        println location.formattedAddress
        println "    ${location.name}" // White House
        println "    ${location.number}" // 1600
        println "    ${location.street}" // Pennsylvania Ave. NW
        println "    ${location.city}" // Washington
        println "    ${location.stateProvince}" // DC
        ... 
        println "    ${location.location.latitude} / ${location.location.longitude}"
    }

### getLocationsFromAddress
Similar to the previous method, except that the search parameter is an address or famous landmark.

    def locations = geomappingService.getLocationsFromAddress("Eiffel Tower, Paris")
    locations.each { location ->
        println location.formattedAddress
        println "    ${location.name}" // Eiffel Town
        println "    ${location.city}" // Paris
        println "    ${location.country}" // FR
        ... 
        println "    ${location.location.latitude} / ${location.location.longitude}"
    }

### toMiles / toKilometers
Very simply converts between miles and kilometers.

    def kilometers = geomappingService.toKilometers(1)
    def miles = geomappingService.toMiles(1)
    println kilometers // ~ 1.6
    println miles // ~ 0.6

***
## Tag Library