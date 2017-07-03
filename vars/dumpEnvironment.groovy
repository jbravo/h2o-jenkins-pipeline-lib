import ai.h2o.ci.Utils
import static ai.h2o.ci.ColorUtils.*

def call(String title = 'Environment') {
    def utils = new Utils()
    def tableUtils = new ai.h2o.ci.TableUtils()
    def data = [
      "Git Describe"    : utils.gitDescribe(),
      "Git Describe All": utils.gitDescribeAll(),
      "Git Branch"   : utils.gitBranch(),
      "Java version" : utils.javaVersion()
    ]

    def table = tableUtils.table2cols(data)

    def header = green("""
    +===================+
       ${title}
    +===================+
    """)

    echo """
    |${header}
    |${table}
    """.stripMargin('|')
}


