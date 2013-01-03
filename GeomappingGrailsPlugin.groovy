class GeomappingGrailsPlugin {
    // the plugin version
    def version = "0.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = "Geomapping Plugin" // Headline display name of the plugin
    def author = "Rafael Chargel"
    def authorEmail = "rchargel@zcarioca.net"
    def description = 'Adds mapping services to your grails project.'

    // URL to the plugin's documentation
    def documentation = "https://github.com/rchargel/geomapping-grails-plugin/blob/master/README.md"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "GPL3"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Rafael Chargel", email: "rchargel@zcarioca.net" ]]

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "GitHub", url: "https://github.com/rchargel/geomapping-grails-plugin/issues" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/rchargel/geomapping-grails-plugin" ]
}
