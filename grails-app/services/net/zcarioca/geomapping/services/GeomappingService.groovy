package net.zcarioca.geomapping.services

import groovy.util.slurpersupport.GPathResult
import net.zcarioca.geomapper.BoundingBox
import net.zcarioca.geomapper.LatLng
import net.zcarioca.geomapper.Location

import org.apache.commons.io.IOUtils
import org.springframework.context.i18n.LocaleContextHolder as LCH

import com.google.code.geocoder.Geocoder
import com.google.code.geocoder.GeocoderRequestBuilder
import com.google.code.geocoder.model.GeocodeResponse
import com.google.code.geocoder.model.GeocoderAddressComponent;
import com.google.code.geocoder.model.GeocoderRequest
import com.google.code.geocoder.model.GeocoderResult

class GeomappingService {
   static transactional = false

   def grailsApplication
   public static final String encoding = "UTF-8"
   public static final double MEAN_RADIUS_OF_EARTH_KILOMETERS = 6371.009

   private static final double AVERAGE_KILOMETERS_IN_DEGREE = 111.132
   private static final double KILOMETERS_IN_LONGITUDE_AT_EQUATOR = 111.3194600331
   private static final double KILOMETERS_IN_ONE_MILE = 1.60934


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
   BoundingBox buildBoundaryBox(double latitude, double longitude, double radiusInKilometers) {
      double latDelta = (radiusInKilometers + 0.01) / AVERAGE_KILOMETERS_IN_DEGREE
      double lngDelta = (radiusInKilometers + 0.01) / (KILOMETERS_IN_LONGITUDE_AT_EQUATOR * Math.cos(Math.toRadians(latitude)))

      return new BoundingBox(new LatLng(latitude + latDelta, longitude - lngDelta),
      new LatLng(latitude - latDelta, longitude + lngDelta))
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
    * @param coordinate1 The first coordinate
    * @param coordinate2 The second coordinate
    * @return Returns the distance in kilometers
    */
   double calculateDistance(LatLng coordinate1, LatLng coordinate2) {
      return calculateDistance(coordinate1.latitude, coordinate1.longitude, coordinate2.latitude, coordinate2.longitude)
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
    * @return Returns the distance in kilometers
    */
   double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2){
      double dLat = Math.toRadians(latitude2 - latitude1)
      double dLng = Math.toRadians(longitude2 - longitude1)
      double lat1 = Math.toRadians(latitude1)
      double lat2 = Math.toRadians(latitude2)

      double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLng/2) * Math.sin(dLng/2) * Math.cos(lat1) * Math.cos(lat2)
      double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
      
      return MEAN_RADIUS_OF_EARTH_KILOMETERS * c
   }

   /**
    * Converts miles to kilometers.
    * @param miles The number of miles
    * @return Returns the distance in kilometers
    */
   double toKilometers(double miles) {
      return miles * KILOMETERS_IN_ONE_MILE
   }

   /**
    * Converts kilometers to miles.
    * @param kilometers The number of kilometers
    * @return Returns the distance in miles.
    */
   double toMiles(double kilometers) {
      return kilometers / KILOMETERS_IN_ONE_MILE
   }

   /**
    * Gets a list of locations from a string
    * @param location The location string
    * @return Returns a list of sorted locations.
    */
   List<Location> getLocationsFromAddress(String address) {
      Geocoder geocoder = new Geocoder()
      GeocoderRequest geocoderRequest = new GeocoderRequestBuilder()
            .setAddress(address)
            .setLanguage(LCH.locale.language)
            .getGeocoderRequest()

      GeocodeResponse geocodeResponse = geocoder.geocode(geocoderRequest)
      return getLocationsFromResponse(geocodeResponse)
   }

   /**
    * Gets a list of locations from a coordinate.
    * @param latitude The latitude
    * @param longitude The longitude
    * @return Returns a list of sorted locations.
    */
   List<Location> getLocationsFromCoordinates(double latitude, double longitude) {
      Geocoder geocoder = new Geocoder()
      GeocoderRequest geocoderRequest = new GeocoderRequestBuilder()
            .setLocation(new com.google.code.geocoder.model.LatLng(latitude, longitude))
            .setLanguage(LCH.locale.language)
            .getGeocoderRequest()
            
      GeocodeResponse geocodeResponse = geocoder.geocode(geocoderRequest)
      return getLocationsFromResponse(geocodeResponse)
   }

   protected List<Location> getLocationsFromResponse(GeocodeResponse geocodeResponse) {
      def locations = []

      geocodeResponse.results.each { result ->
         if (result.geometry?.location) {
            Location location = new Location()
            location.formattedAddress = result.formattedAddress
            location.location = new LatLng(result.geometry.location.lat, result.geometry.location.lng)
            location.partialMatch = result.partialMatch
            
            def addrComps = breakDownAddress(result.addressComponents)
            location.name = addrComps['point_of_interest'] ?: addrComps['establishment']
            location.number = addrComps['street_number']
            location.street = addrComps['route']
            location.city = addrComps['postal_town'] ?:
                            addrComps['administrative_area_level_2'] ?:
                            addrComps['locality'] ?:
                            addrComps['neighborhood']
            location.stateProvince = addrComps['administrative_area_level_1']
            location.postalCode = addrComps['postal_code']
            location.country = addrComps['country']
            locations << location
         }
      }

      return locations
   }
   
   private def breakDownAddress(addrComps) {
      def addr = [:]
      addrComps.each { comp ->
         comp.types.each { type ->
            if (type != 'political') {
               addr[type] = comp.shortName
            }
         }
      }
      return addr
   }
}
