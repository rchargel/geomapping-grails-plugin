package net.zcarioca.geomapping.taglib

import org.apache.commons.lang.StringUtils;

import net.zcarioca.geomapper.LatLng;

class GeomappingTagLib {
   static namespace = "geomapping"
   
   def grailsApplication
   def geomappingService
   
   private static final MAP_TYPES = ['ROADMAP', 'SATELLITE', 'HYBRID', 'TERRAIN']
   
   /**
    * Initializes the map with options in place.
    * The body can be a javascript callback method.
    * 
    * @attr id REQUIRED The element ID of the map canvas.
    * 
    * @attr latitude The initial latitude, defaults to user's location if available.
    * 
    * @attr longitude The initial longitude, defaults to user's location if available.
    * 
    * @attr zoom The initial zoom level, defaults to 12.
    * 
    * @attr type One of 'ROADMAP', 'SATELLITE', 'HYBRID', 'TERRAIN'.  
    *            Defaults to ROADMAP.
    * 
    * @attr args Overrides other arguments. Allows the user to build up a parameter 
    *            map in the controller.
    */
   def initMap = { attrs, body ->
      def args = attrs.args
      
      def apiKey = grailsApplication?.config?.geomapping?.apiKey
      if (!apiKey) {
         throwTagError("The 'geomapping.apiKey' configuration should be added to your project's Config.groovy.")
      }
      
      def mapId = args?.id ?: attrs.id ?: null
      if (!mapId) {
         throwTagError("Error in tag [geomapping:initMap] - attribute ID must be set.")
      }
      def type = args?.type?.toUpperCase() ?: attrs.type?.toUpperCase() ?: "ROADMAP"
      if (!MAP_TYPES.contains(type)) {
         throwTagError("Error in tag [geomapping:initMap] - type must be one of: ${MAP_TYPES}")
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
      
      def writer = new StringWriter()
      writer << body()
      def callback = (writer.toString().trim() =~ /(?m)(^\s+)|(\s+$)/).replaceAll('')
      callback = callback.replaceAll("\n", "")
      
      out << '<script type="text/javascript">\n'
      out << "var geomapOptions = {\n"
      out << "   mapCanvas  : '${mapId}',\n"
      if (StringUtils.isNotBlank(callback)) {
         out << "   callback   : ${callback},\n"
      }
      out << '   mapOptions : {\n'
      out << "      zoom:               ${zoom},\n"
      out << "      panControl:         ${panControl},\n"
      out << "      zoomControl:        ${zoomControl},\n"
      out << "      mapTypeControl:     ${mapTypeControl},\n"
      out << "      scaleControl:       ${scaleControl},\n"
      out << "      streetViewControl:  ${streetViewControl},\n"
      out << "      overviewMapControl: ${overviewMapControl},\n"
      if (startLat && startLng) {
         out << "      center:             [${startLat}, ${startLng}],\n"
      }
      out << "      mapType:            '${type}'\n"
      out << '   }\n'
      out << '};\n'
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
   
}
