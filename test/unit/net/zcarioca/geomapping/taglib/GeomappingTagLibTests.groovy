package net.zcarioca.geomapping.taglib

import static org.junit.Assert.*

import grails.test.GrailsUnitTestCase
import grails.test.mixin.Mock;
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin;
import grails.test.mixin.web.GroovyPageUnitTestMixin;

import net.zcarioca.geomapper.LatLng;
import net.zcarioca.geomapping.services.GeomappingService;

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException;
import org.junit.*


@TestFor(GeomappingTagLib)
@TestMixin(GroovyPageUnitTestMixin)
class GeomappingTagLibTests {

   @Before
   void setUp() {
      mockTagLib(GeomappingTagLib)
      config.geomapping.apiKey = "1234"
      
      def geomappingServiceMock = mockFor(GeomappingService)
      geomappingServiceMock.demand.getCoordinatesFromIP(1..1) { String ipAddress ->
         return new LatLng(42.2, -70.5)
      }
      GeomappingTagLib.metaClass.geomappingService = geomappingServiceMock.createMock()
   }

   @After
   void tearDown() {
      config.geomapping.apiKey = "1234"
   }

   @Test
   void testInit() {
      assertOutputEquals('<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=1234&sensor=false"></script>', '<geomapping:init/>')
   }
   
   @Test(expected = GrailsTagException.class)
   void testInitWithError() {
      config.geomapping.apiKey = null
      assertOutputEquals("doesn't matter", '<geomapping:init/>')
   }

   @Test
   void testInitWithSensor() {
      assertOutputEquals('<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=1234&sensor=true"></script>', '<geomapping:init sensor="true"/>')
   }

   @Test
   void testInitWithCallback() {
      assertOutputEquals('<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=1234&sensor=true&callback=runme"></script>', '<geomapping:init sensor="true" callback="runme"/>')
   }
   
   @Test
   void testMap() {
      def expectedOutput = '''<div id="map_canvas"></div>
<script type="text/javascript">
var mapCanvas = document.getElementById('map_canvas');
var mapOptions = {
   zoom:               12,
   panControl:         true,
   zoomControl:        true,
   mapTypeControl:     true,
   scaleControl:       true,
   streetViewControl:  true,
   overviewMapControl: true,
   center:             new google.maps.LatLng(40,-72),
   mapTypeId:          google.maps.MapTypeId.ROADMAP
};
var map = new google.maps.Map(mapCanvas, mapOptions);
</script>'''
      assertOutputEquals(expectedOutput, '<geomapping:map latitude="40" longitude="-72"/>')
   }
   
   @Test
   void testMapNoLatLng() {
      def expectedOutput = '''<div id="map_canvas"></div>
<script type="text/javascript">
var mapCanvas = document.getElementById('map_canvas');
var mapOptions = {
   zoom:               12,
   panControl:         true,
   zoomControl:        true,
   mapTypeControl:     true,
   scaleControl:       true,
   streetViewControl:  true,
   overviewMapControl: true,
   center:             new google.maps.LatLng(42.2,-70.5),
   mapTypeId:          google.maps.MapTypeId.ROADMAP
};
var map = new google.maps.Map(mapCanvas, mapOptions);
</script>'''
      assertOutputEquals(expectedOutput, '<geomapping:map />')
   }
   
   @Test
   void testMapDifferentId() {
      def expectedOutput = '''<div id="test_different_id" title="My Map" class="myMapClass"></div>
<script type="text/javascript">
var mapCanvas = document.getElementById('test_different_id');
var mapOptions = {
   zoom:               12,
   panControl:         true,
   zoomControl:        true,
   mapTypeControl:     true,
   scaleControl:       true,
   streetViewControl:  true,
   overviewMapControl: true,
   center:             new google.maps.LatLng(40,-72),
   mapTypeId:          google.maps.MapTypeId.ROADMAP
};
var map = new google.maps.Map(mapCanvas, mapOptions);
</script>'''
      assertOutputEquals(expectedOutput, '<geomapping:map id="test_different_id" title="My Map" class="myMapClass" latitude="40" longitude="-72"/>')
   }
   
   @Test
   void testMapSatellite() {
      def expectedOutput = '''<div id="map_canvas"></div>
<script type="text/javascript">
var mapCanvas = document.getElementById('map_canvas');
var mapOptions = {
   zoom:               12,
   panControl:         true,
   zoomControl:        true,
   mapTypeControl:     true,
   scaleControl:       true,
   streetViewControl:  true,
   overviewMapControl: true,
   center:             new google.maps.LatLng(40,-72),
   mapTypeId:          google.maps.MapTypeId.SATELLITE
};
var map = new google.maps.Map(mapCanvas, mapOptions);
</script>'''
      assertOutputEquals(expectedOutput, '<geomapping:map latitude="40" longitude="-72" type="satellite"/>')
   }
   
   @Test
   void testMapChangeControlParameters() {
      config.geomapping.control.pan = "false"
      config.geomapping.control.mapType = false
      def expectedOutput = '''<div id="map_canvas"></div>
<script type="text/javascript">
var mapCanvas = document.getElementById('map_canvas');
var mapOptions = {
   zoom:               12,
   panControl:         false,
   zoomControl:        true,
   mapTypeControl:     false,
   scaleControl:       true,
   streetViewControl:  true,
   overviewMapControl: true,
   center:             new google.maps.LatLng(40,-72),
   mapTypeId:          google.maps.MapTypeId.ROADMAP
};
var map = new google.maps.Map(mapCanvas, mapOptions);
</script>'''
      assertOutputEquals(expectedOutput, '<geomapping:map latitude="40" longitude="-72"/>')
      config.geomapping.control.pan = null
      config.geomapping.control.mapType = null
   }
   
   @Test(expected = GrailsTagException.class)
   void testMapBadType() {
      assertOutputEquals('does not matter', '<geomapping:map latitude="40" longitude="-72" type="awesome_map"/>')
   }
   
   @Test
   void testMapArgs() {
      def expectedOutput = '''<div id="map_canvas"></div>
<script type="text/javascript">
var mapCanvas = document.getElementById('map_canvas');
var mapOptions = {
   zoom:               7,
   panControl:         true,
   zoomControl:        false,
   mapTypeControl:     true,
   scaleControl:       true,
   streetViewControl:  false,
   overviewMapControl: true,
   center:             new google.maps.LatLng(-50,25),
   mapTypeId:          google.maps.MapTypeId.TERRAIN
};
var map = new google.maps.Map(mapCanvas, mapOptions);
myCallback(map);
</script>'''
      assertOutputEquals(expectedOutput, '<geomapping:map latitude="40" longitude="-72" args="${[zoom:7,type:"terrain",callback:"myCallback",zoomControl:"false",streetViewControl:false,latitude:-50,longitude:25]}"/>')
   }
   
}
