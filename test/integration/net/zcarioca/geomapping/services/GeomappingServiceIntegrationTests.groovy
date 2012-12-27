package net.zcarioca.geomapping.services

import static org.junit.Assert.*
import grails.test.mixin.TestFor;

import net.zcarioca.geomapper.BoundingBox;
import net.zcarioca.geomapper.LatLng;

import org.junit.*

@TestFor(GeomappingService)
class GeomappingServiceIntegrationTests {

   GeomappingService geomappingService

   @Test
   void testGetLocationForIP() {
      LatLng ipPos = geomappingService.getCoordinatesFromIP('74.125.128.102') // google
      assertEquals(37.419201, ipPos.latitude, 0)
      assertEquals(-122.057404, ipPos.longitude, 0)
      ipPos = geomappingService.getCoordinatesFromIP('157.166.255.18') // CNN
      assertEquals(33.749001, ipPos.latitude, 0)
      assertEquals(-84.388, ipPos.longitude, 0)
   }
   
   @Test
   void testGetLocationsFromCoordinates() {
      def locations = geomappingService.getLocationsFromCoordinates(40.7482845, -73.9855692)
      assertEquals('5th Ave', locations[0].street)
      locations = geomappingService.getLocationsFromCoordinates(48.8582780, 2.2942540)
      assertEquals('Av. Anatole France', locations[0].street)
      locations = geomappingService.getLocationsFromCoordinates(51.5007299, -0.1246827)
      assertEquals('Bridge St', locations[0].street)
      locations = geomappingService.getLocationsFromCoordinates(38.8976777, -77.0365170)
      assertEquals('White House', locations[0].name)
   }

   @Test
   void testGetLocationsFromAddress() {
      def locations = geomappingService.getLocationsFromAddress("Empire State Building")
      assertEquals(40.7482845d, locations[0].location.latitude, 0.0001d)
      assertEquals(-73.9855692d, locations[0].location.longitude, 0.0001d)
      locations = geomappingService.getLocationsFromAddress("eiffel tower, paris")
      assertEquals(48.8582780d, locations[0].location.latitude, 0.0001d)
      assertEquals(2.2942540d, locations[0].location.longitude, 0.0001d)
      locations = geomappingService.getLocationsFromAddress("Big Ben, London")
      assertEquals(51.5007299d, locations[0].location.latitude, 0.0001d)
      assertEquals(-0.1246827d, locations[0].location.longitude, 0.0001d)
      locations = geomappingService.getLocationsFromAddress("1600 Pennsylvania Ave. Washington DC")
      assertEquals(38.8976777d, locations[0].location.latitude, 0.0001d)
      assertEquals(-77.0365170d, locations[0].location.longitude, 0.0001d)
   }
}
