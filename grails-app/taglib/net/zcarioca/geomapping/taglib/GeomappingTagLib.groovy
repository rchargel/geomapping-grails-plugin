package net.zcarioca.geomapping.taglib

import net.zcarioca.geomapper.LatLng;

class GeomappingTagLib {
   static namespace = "geomapping"
   
   def grailsApplication
   def geomappingService

   /**
    * Initializes the google maps api.
    * 
    * @emptyTag
    * 
    * @attr sensor Set to true or false. Determines whether this application uses a sensor
    *              to determine the user's location. Defaults to false.
    */
   def init = { attrs ->
      def apiKey = grailsApplication?.config?.geomapping?.apiKey
      def sensor = attrs.sensor ?: "false"
      
      if (!apiKey) {
         throwTagError("The 'geomapping.apiKey' configuration should be added to your project's Config.groovy.")
      }
      out << "<script type=\"text/javascript\" src=\"https://maps.googleapis.com/maps/api/js?key=${apiKey}&sensor=${sensor}\"></script>"
   }
   
   /**
    * Adds the google map to your page.
    * 
    * @emptyTag
    * 
    * @attr id The element ID of the map canvas. Defaults to 'map_canvas'.
    * @attr latitude The initial latitude, defaults to user's location if available.
    * @attr longitude The initial longitude, defaults to user's location if available.
    * @attr zoom The initial zoom level, defaults to 12.
    * @attr type One of 'ROADMAP', 'SATELLITE', 'HYBRID', 'TERRAIN'.  Defaults to ROADMAP.
    */
   def map = { attrs ->
      def mapId = 'map_canvas'
      if (attrs['id']) {
         mapId = attrs.remove('id')
      }
      
      def type = "ROADMAP"
      if (attrs.type) {
         type = attrs.remove('type')
      }
      def zoom = "12"
      if (attrs.zoom) {
         zoom = attrs.remove('zoom')
      }
      def startLat = null
      def startLng = null
      if (attrs.latitude && attrs.longitude) {
         startLat = attrs.remove('latitude')
         startLng = attrs.remove('longitude')
      } else {
         LatLng pos = geomappingService.getLocationOfIP(request.remoteAddr)
         if (pos) {
            startLat = pos.latitude
            startLng = pos.longitude
         }
      }
      def panControl = grailsApplication?.config?.geomapping?.features?.pan ?: "true"
      def zoomControl = grailsApplication?.config?.geomapping?.features?.zoom ?: "true"
      def mapTypeControl = grailsApplication?.config?.geomapping?.features?.mapType ?: "true"
      def scaleControl = grailsApplication?.config?.geomapping?.features?.scale ?: "true"
      def streetViewControl = grailsApplication?.config?.geomapping?.features?.streetView ?: "true"
      def overviewMapControl = grailsApplication?.config?.geomapping?.features?.overviewMap ?: "true"
      
      out << "<div id=\"${mapId}\""
      outputAttrs(attrs, out)
      out << '></div>'
      out << '<script type="text/javascript">'
      out << "var mapCanvas = document.getElementById('${mapId}'); "
      out << 'var mapOptions = { '
      out << "zoom: ${zoom}, "
      out << "panControl: ${panControl}, "
      out << "zoomControl: ${zoomControl}, "
      out << "mapTypeControl: ${mapTypeControl}, "
      out << "scaleControl: ${scaleControl}, "
      out << "streetViewControl: ${streetViewControl}, "
      out << "overviewMapControl: ${overviewMapControl}, "
      if (startLat && startLng) {
         out << "center: new google.maps.LatLng(${startLat},${strtLng}), "
      }
      out << "mapTypeId: google.maps.MapTypeId.${type} "
      out << '}; '
      out << 'var map = new google.maps.Map(mapCanvas, mapOptions); '
      out << '</script>'
   }
   
   private def outputAttrs(attrs, writer) {
      attrs.each { key, value ->
         writer << " ${key}=\"${value}\""
      }
   }
}
