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
      geomappingServiceMock.demand.getCoordinatesFromIP(1..3) { String ipAddress ->
         return new LatLng(42.2, -70.5)
      }
      GeomappingTagLib.metaClass.geomappingService = geomappingServiceMock.createMock()
   }

   @After
   void tearDown() {
      config.geomapping.apiKey = "1234"
   }

   @Test
   void testInitMap() {
      def expected = '''<script type="text/javascript">
var geomapOptions = {
   mapCanvas  : 'map_canvas',
   mapOptions : {
      zoom:               12,
      panControl:         true,
      zoomControl:        true,
      mapTypeControl:     true,
      scaleControl:       true,
      streetViewControl:  true,
      overviewMapControl: true,
      center:             [42.2, -70.5],
      mapType:            'ROADMAP'
   }
};
</script>'''
      def tag = '<geomapping:initMap id="map_canvas"/>'
      def output = applyTemplate("${tag}")
      println "input:\n${tag}\n"
      println "output:\n${output}"
      assertEquals(expected, output)
   }
   
   @Test
   void testInitMapWithLatLng() {
      def expected = '''<script type="text/javascript">
var geomapOptions = {
   mapCanvas  : 'map_canvas',
   mapOptions : {
      zoom:               12,
      panControl:         true,
      zoomControl:        true,
      mapTypeControl:     true,
      scaleControl:       true,
      streetViewControl:  true,
      overviewMapControl: true,
      center:             [40, -70],
      mapType:            'ROADMAP'
   }
};
</script>'''
      def tag = '<geomapping:initMap id="map_canvas" latitude="40" longitude="-70"/>'
      def output = applyTemplate("${tag}")
      println "input:\n${tag}\n"
      println "output:\n${output}"
      assertEquals(expected, output)
   }

   @Test
   void testInitMapWithProps() {
      config.geomapping.control.pan=false
      config.geomapping.control.scale="false"
      
      def expected = '''<script type="text/javascript">
var geomapOptions = {
   mapCanvas  : 'map_canvas',
   mapOptions : {
      zoom:               12,
      panControl:         false,
      zoomControl:        true,
      mapTypeControl:     true,
      scaleControl:       false,
      streetViewControl:  true,
      overviewMapControl: true,
      center:             [42.2, -70.5],
      mapType:            'ROADMAP'
   }
};
</script>'''
      def tag = '<geomapping:initMap id="map_canvas"/>'
      def output = applyTemplate("${tag}")
      println "input:\n${tag}\n"
      println "output:\n${output}"
      assertEquals(expected, output)
      config.geomapping.control.pan=null
      config.geomapping.control.scale=null
   }
   
   @Test
   void testInitMapWithBody() {
      def expected = '''<script type="text/javascript">
var geomapOptions = {
   mapCanvas  : 'map_canvas',
   callback   : function (mapObj) {alert('Map is loaded');},
   mapOptions : {
      zoom:               12,
      panControl:         true,
      zoomControl:        true,
      mapTypeControl:     true,
      scaleControl:       true,
      streetViewControl:  true,
      overviewMapControl: true,
      center:             [42.2, -70.5],
      mapType:            'ROADMAP'
   }
};
</script>'''
      def tag = '''<geomapping:initMap id="map_canvas">
function (mapObj) {
   alert('Map is loaded');
}
</geomapping:initMap>'''
      def output = applyTemplate("${tag}")
      println "input:\n${tag}\n"
      println "output:\n${output}"
      assertEquals(expected, output)
   }

   @Test
   void testInitMapWithType() {
      def expected = '''<script type="text/javascript">
var geomapOptions = {
   mapCanvas  : 'map_canvas',
   mapOptions : {
      zoom:               12,
      panControl:         true,
      zoomControl:        true,
      mapTypeControl:     true,
      scaleControl:       true,
      streetViewControl:  true,
      overviewMapControl: true,
      center:             [42.2, -70.5],
      mapType:            'SATELLITE'
   }
};
</script>'''
      def tag = '<geomapping:initMap id="map_canvas" type="satellite"/>'
      def output = applyTemplate("${tag}")
      println "input:\n${tag}\n"
      println "output:\n${output}"
      assertEquals(expected, output)
   }

   @Test
   void testInitMapWithArgs() {
      def expected = '''<script type="text/javascript">
var geomapOptions = {
   mapCanvas  : 'map_canvas',
   mapOptions : {
      zoom:               7,
      panControl:         true,
      zoomControl:        false,
      mapTypeControl:     true,
      scaleControl:       true,
      streetViewControl:  true,
      overviewMapControl: true,
      center:             [40, -70],
      mapType:            'TERRAIN'
   }
};
</script>'''
      def tag = '<geomapping:initMap id="map_canvas" args="${[zoom: 7, latitude: 40, longitude: -70, zoomControl: false, type: "terrain"]}"/>'
      def output = applyTemplate("${tag}")
      println "input:\n${tag}\n"
      println "output:\n${output}"
      assertEquals(expected, output)
   }
   
   @Test(expected = GrailsTagException.class)
   void testInitMapNoId() {
      def tag = '<geomapping:initMap />'
      println "input:\n${tag}"
      applyTemplate("${tag}")
   }
   
   @Test(expected = GrailsTagException.class)
   void testInitMapBadType() {
      def tag = '<geomapping:initMap id="map_canvas" type="cool_map" />'
      println "input:\n${tag}"
      applyTemplate("${tag}")
   }
   
   @Test(expected = GrailsTagException.class)
   void testInitMapNoApiKey() {
      config.geomapping.apiKey=null
      def tag = '<geomapping:initMap id="map_canvas" />'
      println "input:\n${tag}"
      applyTemplate("${tag}")
   }
   
}
