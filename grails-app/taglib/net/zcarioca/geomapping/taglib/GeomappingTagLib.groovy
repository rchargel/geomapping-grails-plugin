package net.zcarioca.geomapping.taglib

import org.apache.commons.lang.StringUtils;

import net.zcarioca.geomapper.LatLng;

class GeomappingTagLib {
   static namespace = "geomapping"
   
   def grailsApplication
   def geomappingService
   
   private static final MAP_TYPES = ['ROADMAP', 'SATELLITE', 'HYBRID', 'TERRAIN']

   /**
    * Initializes the google maps api.
    * 
    * @emptyTag
    * 
    * @attr sensor Set to true or false. Determines whether this application uses a sensor
    *              to determine the user's location. Defaults to false.
    * 
    * @attr callback The callback to call when loaded.  Not required.
    */
   def init = { attrs ->
      def apiKey = grailsApplication?.config?.geomapping?.apiKey
      def sensor = attrs.sensor ?: "false"
      
      if (!apiKey) {
         throwTagError("The 'geomapping.apiKey' configuration should be added to your project's Config.groovy.")
      }
      def url = "https://maps.googleapis.com/maps/api/js?key=${apiKey}&sensor=${sensor}"
      if (attrs.callback) {
         url = "${url}&callback=${attrs.callback}"
      }
      out << "<script type=\"text/javascript\" src=\"${url}\"></script>"
   }
   
   /**
    * Adds the google map to your page.
    * 
    * @emptyTag
    * 
    * @attr id The element ID of the map canvas. Defaults to 'map_canvas'.
    * @attr callback The callback method to run when the map is loaded.  Not required.
    * @attr latitude The initial latitude, defaults to user's location if available.
    * @attr longitude The initial longitude, defaults to user's location if available.
    * @attr zoom The initial zoom level, defaults to 12.
    * @attr type One of 'ROADMAP', 'SATELLITE', 'HYBRID', 'TERRAIN'.  Defaults to ROADMAP.
    * @attr args Overrides other arguments. Allows the user to build up a parameter map in the controller.
    */
   def map = { attrs ->
      def apiKey = grailsApplication?.config?.geomapping?.apiKey
      if (!apiKey) {
         throwTagError("The 'geomapping.apiKey' configuration should be added to your project's Config.groovy.")
      }
      
      def args = attrs.args
      def mapId = args?.id ?: attrs.id ?: 'map_canvas'
      def type = args?.type?.toUpperCase() ?: attrs.type?.toUpperCase() ?: "ROADMAP"
      if (!MAP_TYPES.contains(type)) {
         throwTagError("Error in tag [geomapping:map] type must be one of: ${MAP_TYPES}")
      }
      def zoom = args?.zoom ?: attrs.zoom ?: 12
      
      def startLat = args?.latitude ?: attrs.latitude ?: null
      def startLng = args?.longitude ?: attrs.longitude ?: null
      if (startLat == null || startLng == null) {
         LatLng pos = geomappingService.getCoordinatesFromIP(request.remoteAddr)
         if (pos) {
            startLat = pos.latitude
            startLng = pos.longitude
         }
      }
      def panControl = getControl(args, 'pan')
      def zoomControl = getControl(args, 'zoom')
      def mapTypeControl = getControl(args, 'mapType')
      def scaleControl = getControl(args, 'scale')
      def streetViewControl = getControl(args, 'streetView')
      def overviewMapControl = getControl(args, 'overviewMap')
      
      out << "<div id=\"${mapId}\""
      outputAttrs(attrs, out)
      out << '></div>\n'
      out << '<script type="text/javascript">\n'
      out << "var mapCanvas = document.getElementById('${mapId}');\n"
      out << 'var mapOptions = {\n'
      out << "   zoom:               ${zoom},\n"
      out << "   panControl:         ${panControl},\n"
      out << "   zoomControl:        ${zoomControl},\n"
      out << "   mapTypeControl:     ${mapTypeControl},\n"
      out << "   scaleControl:       ${scaleControl},\n"
      out << "   streetViewControl:  ${streetViewControl},\n"
      out << "   overviewMapControl: ${overviewMapControl},\n"
      if (startLat && startLng) {
         out << "   center:             new google.maps.LatLng(${startLat},${startLng}),\n"
      }
      out << "   mapTypeId:          google.maps.MapTypeId.${type}\n"
      out << '};\n'
      out << 'var map = new google.maps.Map(mapCanvas, mapOptions);\n'
      def callback = args?.callback ?: attrs.callback ?: null
      if (callback) {
         out << "${callback}(map);\n"
      }
      out << '</script>'
   }
   
   private String getControl(def args, def control) {
      def value = null
      if (args) {
         def controlKey = "${control}Control"
         if (args[controlKey] != null) {
            value = args[controlKey].toString()
         }
      }
      if (!value) {
         value = grailsApplication?.config?.geomapping?.control?."${control}"?.toString()
         if (value != 'true' && value != 'false') {
            value = null
         }
      }
      return value ?: 'true'
   }
   
   private def outputAttrs(attrs, writer) {
      def excluded = ['id','type','zoom','latitude', 'longitude', 'args', 'callback']
      attrs.each { key, value ->
         if (!excluded.contains(key)) {
            writer << " ${key}=\"${value}\""
         }
      }
   }
}
