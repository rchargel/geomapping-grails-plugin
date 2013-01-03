//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
//
//    ant.mkdir(dir:"${basedir}/grails-app/jobs")
//

updateConfig()

private void updateConfig() {
   def configFile = new File(basedir, 'grails-app/conf/Config.groovy')
   if (configFile.exists() && configFile.text.indexOf("geomapping") == -1) {
      configFile.withWriterAppend { file ->
         file.writeLine '\n// Added by the geomapping plugin:'
         file.writeLine 'geomapping.apiKey="" // TODO add api key'
      }
      println '''
************************************************************
* Your grails-app/conf/Config.groovy has been updated with *
* default configurations of geomapping plugin;             *
* please set your API Key.                                 *
************************************************************
'''
   }
}