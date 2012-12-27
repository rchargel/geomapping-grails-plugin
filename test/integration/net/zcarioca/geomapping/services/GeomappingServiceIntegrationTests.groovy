package net.zcarioca.geomapping.services

import static org.junit.Assert.*
import grails.test.mixin.TestFor;

import net.zcarioca.geomapper.LatLng;

import org.junit.*

@TestFor(GeomappingService)
class GeomappingServiceIntegrationTests {

   GeomappingService geomappingService

   @Test
   void testCalculateDistance() {
      LatLng newYork = new LatLng(40.714353, -74.005974)
      LatLng losAngeles = new LatLng(34.052236, -118.243673)
      LatLng london = new LatLng(51.507335, -0.127683)
      LatLng johannesburg = new LatLng(-26.204074, 28.047292)
      LatLng midtown = new LatLng(40.760439, -73.979336)
      
      assertEquals(3936, geomappingService.calculateDistance(newYork, losAngeles), 0.5)
      assertEquals(3936, geomappingService.calculateDistance(losAngeles, newYork), 0.5)
      assertEquals(5570, geomappingService.calculateDistance(newYork, london), 0.5)
      assertEquals(5570, geomappingService.calculateDistance(london, newYork), 0.5)
      assertEquals(12840, geomappingService.calculateDistance(johannesburg, newYork), 0.5)
      assertEquals(12840, geomappingService.calculateDistance(newYork, johannesburg), 0.5)
      assertEquals(5.6, geomappingService.calculateDistance(newYork, midtown), 0.1)
      assertEquals(5.6, geomappingService.calculateDistance(midtown, newYork), 0.1)
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
