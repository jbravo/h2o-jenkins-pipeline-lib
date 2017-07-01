import ai.h2o.ci.Utils

def call(String title = 'Environment') {
    def utils = new Utils()

    echo """
    \033[1;33m
    +===================+
      ${title}
    +===================+
    Git Describe: ${utils.gitDescribeAll()} 
    \033[0m
    """

    //Java version: ${utils.javaVersion()}
    /*sh """
       echo -e "\nJava version:\n$(java -version)"
       echo -e "\nEnvironment:\n$(env)"
       """*/
}


