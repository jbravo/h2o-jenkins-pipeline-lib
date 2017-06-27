#!/usr/bin/groovy
@GrabResolver(name="gradle", root="http://repo.gradle.org/gradle/libs-releases-local")
@Grab("org.gradle:gradle-tooling-api:4.0")
@Grab("org.slf4j:slf4j-simple:1.6.4")

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.model.GradleProject
import java.io.File

/**
 * s3up 
 *  - groupId:String 
 *  - artifactId: String
 *  - version: String
 *  - localArtifact:String 
 */
def call(body) {
    def config = [
        groupId: "ai.h2o",
        remoteArtifactBucket:"s3://s3n-test/s3up3", 
        credentialsId:"awsArtifactsUploader"
		workspaceDir:env.WORKSPACE]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

	def buildDir = new File("${conf.workspaceDir}/xxx")
	def buildFile = new File(builDir, "build.dir")
	buildDir.mkdirs()
	writeFile(new File(buildDir, "build.gradle"), buildProjectFileContent)

    withCredentials([[ $class: 'AmazonWebServicesCredentialsBinding', credentialsId: config.credentialsId]]) {
        sh """
        cat "${buildFile}"
        """
    }
}

@NonCPS
def buildProjectFileContent = """
plugins {
  id 'ivy-publish'
}
"""

@NonCPS
def withClosable = { closable, closure ->
    try {
        closure(closable)
    } finally {
        closable.close()
    }
}

@NonCPS
def writeFile(File destination, String content) throws IOException {
	BufferedWriter output = null;
	try {
		output = new BufferedWriter(new FileWriter(destination));
		output.write(content);
	} finally {
		if (output != null) {
			output.close();
		}
	}
}

