package net.zcarioca.geomapping.services



import grails.test.mixin.*
import net.zcarioca.geomapper.BoundingBox;
import net.zcarioca.geomapper.LatLng;

import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(GeomappingService)
class GeomappingServiceTests {
 
   GeomappingService geomappingService = new GeomappingService()
   
   @Test
   void testToMiles() {
      assertEquals(0.621371, geomappingService.toMiles(1), 0.001)
      assertEquals(68.9722, geomappingService.toMiles(111), 0.001)
      assertEquals(76.71200191, geomappingService.toMiles(123.456), 0.001)
   }
   
   @Test
   void testToKilometers() {
      assertEquals(1.60934, geomappingService.toKilometers(1), 0.001)
      assertEquals(178.637, geomappingService.toKilometers(111), 0.001)
      assertEquals(198.6831729, geomappingService.toKilometers(123.456), 0.001)
   }
   
   @Test
   void testBuildBoundingBox() {
      LatLng newYork = new LatLng(40.714353, -74.005974)
      BoundingBox bb = geomappingService.buildBoundaryBox(newYork.latitude, newYork.longitude, 6);
      assertTrue(bb.containsCoordinate(newYork))
      assertEquals(6, geomappingService.calculateDistance(newYork.latitude, newYork.longitude, bb.northWest.latitude, newYork.longitude), 0.1);
      assertEquals(6, geomappingService.calculateDistance(newYork.latitude, newYork.longitude, bb.southEast.latitude, newYork.longitude), 0.1);
      assertEquals(6, geomappingService.calculateDistance(newYork.latitude, newYork.longitude, newYork.latitude, bb.northWest.longitude), 0.1);
      assertEquals(6, geomappingService.calculateDistance(newYork.latitude, newYork.longitude, newYork.latitude, bb.southEast.longitude), 0.1);
      assertEquals(6, geomappingService.calculateDistance(newYork.latitude, newYork.longitude, bb.northWest.latitude, bb.northWest.longitude), 3);
      assertEquals(6, geomappingService.calculateDistance(newYork.latitude, newYork.longitude, bb.southEast.latitude, bb.southEast.longitude), 3);
   }

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
}
