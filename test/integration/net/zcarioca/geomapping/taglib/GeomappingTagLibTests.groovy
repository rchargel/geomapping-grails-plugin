package net.zcarioca.geomapping.taglib

import static org.junit.Assert.*

import grails.test.GrailsUnitTestCase
import grails.test.mixin.Mock;
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin;
import grails.test.mixin.web.GroovyPageUnitTestMixin;

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
   
}
