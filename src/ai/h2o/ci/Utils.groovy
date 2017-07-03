package ai.h2o.ci

def getShell() {
    new shell()
}

/**
 * Return version suffix based on current branch
 * and environment variable BUILD_ID.
 */
def getCiVersionSuffix() {
    return "${env.BRANCH_NAME}_${env.BUILD_ID}"
}

def getCommmandOutput(String cmd) {
    return getShell().pipe(cmd).trim()
}

def gitDescribeAll() {
    return getShell().pipe("git describe --all --long HEAD").trim()
}

def gitDescribe() {
    return getShell().pipe("git describe --always --long HEAD || echo none").trim()
}

def javaVersion() {
    return getShell().pipe("java -version &2>1 || echo not found").trim()
}

def gitBranch() {
    return getShell().pipe("git rev-parse --abbrev-ref HEAD").trim()
}

/**
 * Version is given as X.Y.Z
 * It returns a tuple (X.Y, Z).
 */
 @NonCPS
def fragmentVersion(String version) {
    def xyPartRgx = /\d+.\d+/
    def zPartRgx = /\d+.*/
    def versionRgx = /($xyPartRgx).($zPartRgx)/
    def matcher = (version =~ versionRgx)
    return new Tuple(matcher[0][1], matcher[0][2])
}

return this
