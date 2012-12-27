package net.zcarioca.geomapping.services

import groovy.util.slurpersupport.GPathResult;

import org.apache.commons.io.IOUtils

class GeomappingService {
   static transactional = false

   def grailsApplication
   def encoding = "UTF-8"
   def AVERAGE_MILES_IN_DEGREE = 69.054068
   def MILES_IN_LONGITUDE_AT_EQUATOR = 69.1707056
   def RADIUS_OF_EARTH_MILES = 3963.1676
   def MILES_TO_KILOMETERS = 1.60934


   /**
    * Creates a course bounding box around a latitude and longitude.  Errors on the side of being too large.
    * 
    * The returned box contains a north, south, east and west coordinates:
    * def boundingBox = buildBoundaryBox(lat, lon, miles)
    * def northLat = boundingBox.north
    * def eastLon = boundingBox.east
    * def southLat = boundingBox.south
    * def westLon = boundingBox.west
    * 
    * @param latitude The latitude
    * @param longitude The longitude
    * @param miles The number of miles to include
    * @return Returns a map containing north, south, east, and west coordinates. 
    */
   def buildBoundaryBox(latitude, longitude, miles) {
      def latDelta = (miles + 1) / AVERAGE_MILES_IN_DEGREE
      def lngDelta = (miles + 1) / (MILES_IN_LONGITUDE_AT_EQUATOR * Math.cos(Math.toRadians(latitude)))

      return [ north: latitude + latDelta, south: latitude - latDelta,
         east: longitude + lngDelta, west: longitude - lngDelta]
   }

   /**
    * Calculates the great circle distance between two points.  This is the shortest distance that can be traversed over
    * a spherical shape.
    * 
    * Uses the Haversine formula
    * a = sin²(Δlat/2) + cos(lat1).cos(lat2).sin²(Δlong/2)
    * c = 2.atan2(√a, √(1−a))
    * d = R.c
    * 
    * where R = radius of the earth
    * all angles are in radians
    * 
    * Note: while this formula does use a lot of trig, the assumption is that it is 
    * unlikely that the user will be processing tens of thousands at a time.  That 
    * being said, even at 10,000 comparisons, this formula is still well within
    * the acceptable sub-second timing.
    * 
    * @param latitude1 The first latitude
    * @param longitude1 The first longitude
    * @param latitude2 The second latitude
    * @param longitude2 The second longitude
    * @return Returns the distance in miles
    */
   double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2){
      double dLat = Math.toRadians(latitude2 - latitude1)
      double dLng = Math.toRadians(longitude2 - longitude1)
      double lat1 = Math.toRadians(latitude1)
      double lat2 = Math.toRadians(latitude2)

      double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLng/2) * Math.sin(dLng/2) * Math.cos(lat1) * Math.cos(lat2)
      double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
      return RADIUS_OF_EARTH_MILES * c
   }

   /**
    * Converts miles to kilometers.
    * @param miles The number of miles
    * @return Returns the distance in kilometers
    */
   double toKilometers(double miles) {
      return miles * MILES_TO_KILOMETERS
   }

   /**
    * Gets a list of locations from a string
    * @param location The location string
    * @return Returns a list of sorted locations.
    */
   def getLocationsFromAddress(location) {
      def xml = getXmlFromStringAddress(location)
      return getLocationsFromXml(xml)
   }
   
   /**
    * Gets a list of locations from a coordinate.
    * @param latitude The latitude
    * @param longitude The longitude
    * @return Returns a list of sorted locations.
    */
   def getLocationsFromCoordinates(latitude, longitude) {
      def xml = getXmlFromLatLng(latitude, longitude)
      return getLocationsFromXml(xml)
   }
   
   /**
    * Gets a list of locations from a given XML document.
    * @param xml The xml document
    * @return Returns a list of locations
    */
   protected def getLocationsFromXml(GPathResult xml) {
      def locations = []

      for (int i in 0..xml.result.size()-1) {
         def result = xml.result[i]

         def addrMap = ['name':'']

         for (int j in 0..result.address_component.size()-1) {
            def addComp = result.address_component[j]

            def type = getType(addComp)
            if (type != 'name') {
               addrMap[type] = addComp.short_name.toString()

               if (type == 'administrative_area_level_1') {
                  addrMap['state'] = addComp.short_name.toString()
               }
            } else {
               addrMap[type] += ", ${addComp.short_name}"
            }
         }
         def geometry = [  
            latitude: Double.parseDouble(result.geometry?.location?.lat?.toString() ?: '0'),
            longitude: Double.parseDouble(result.geometry?.location?.lng?.toString() ?: '0') 
         ]

         if (geometry.latitude == 0 && geometry.longitude == 0) {
            continue
         }

         def street = addrMap['street_number']
         def route = addrMap['route']
         def loc = [:]
         
         if (addrMap['name']) {
            loc['name'] = addrMap['name'].substring(2)
         }
         loc['address'] = "${street} ${route}".replace("null", "").trim()
         if (addrMap['state']) {
            loc['stateProvince'] = addrMap['state']
         }
         if (addrMap['postal_code']) {
            loc['postalCode'] = addrMap['postal_code']
         }
         def city = addrMap['postal_town'] ?: 
                    addrMap['administrative_area_level_2'] ?: 
                    addrMap['locality'] ?: 
                    addrMap['neighborhood'] ?: null
         if (city) {
            loc['city'] = city
         }
         if (addrMap['country']) {
            loc['country'] = addrMap['country']
         }
         loc['latitude'] = geometry.latitude
         loc['longitude'] = geometry.longitude
         
         locations << loc
      }
      return locations
   }
   
   /**
    * Get XML from the String address
    * @param address The address to geocode.
    * @return Returns the xml from the address.
    */
   protected GPathResult getXmlFromStringAddress(String address) {
      def encodedString = URLEncoder.encode(address, encoding)
      def googleGeocoderUrl = getUrl()
      def url = new URL("${googleGeocoderUrl}&address=${encodedString}")

      return getXmlFromUrl(url)
   }
   
   /**
    * Gets the XML for a latitude and longitude.
    * @param latitude The latitude.
    * @param longitude The longitude.
    * @return Returns the XML for the latitude/longitude.
    */
   protected GPathResult getXmlFromLatLng(double latitude, double longitude) {
      def encodedString = URLEncoder.encode("${latitude},${longitude}", encoding)
      def googleGeocoderUrl = getUrl()
      
      def url = new URL("${googleGeocoderUrl}&latlng=${encodedString}")
      
      return getXmlFromUrl(url)
   }

   /**
    * Converts the provided Location from Google into a sing string.
    * @param location The location.
    * @return Returns a string.
    */
   protected String getStringFromLocation(def location) {
      def str = "${location.address ?: ''}, ${location.city ?: ''}, ${location.state?.code ?: ''} ${location.zip ?: ''}"
      str = str.trim().replaceAll("\\s+", " ")
      while (str =~ /^, /) {
         str = str.substring(2)
      }
      return str.replaceAll('\\s+,', ',')
   }


   /**
    * Gets the XML from the provided URL.
    * @param url The URL.
    * @return Returns the XML from the provided URL.
    */
   protected GPathResult getXmlFromUrl(URL url) {
      def stream = url.openStream()
      def xml = new XmlSlurper().parse(stream)

      IOUtils.closeQuietly(stream)

      return xml
   }

   /**
    * Gets the Google geocoder URL.
    * @return Returns the URL of the google GEOCoder.
    */
   protected String getUrl() {
      return grailsApplication.config.geomapping.geocoder.url ?: 'http://maps.googleapis.com/maps/api/geocode/xml?sensor=false'
   }
   
   private String getType(def addrComp) {
      def type = ''
      for (int i in 0..addrComp.type.size()-1) {
         def theType = addrComp.type[i].toString()
         if (theType) {
            if (theType == 'establishment' || theType == 'point_of_interest') {
               return 'name'
            } else if (theType != 'political') {
               type = theType
            }
         }
      }
      return type
   }
}
