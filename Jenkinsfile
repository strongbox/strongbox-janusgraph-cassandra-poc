@Library('jenkins-shared-libraries') _

pipeline {
    agent {
        label "alpine-jdk8-mvn3.6"
    }
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '100')
        timeout(time: 2, unit: 'HOURS')
        disableResume()
        durabilityHint 'PERFORMANCE_OPTIMIZED'
        disableConcurrentBuilds()
        skipStagesAfterUnstable()
    }
    stages {
        stage('Node') {
            steps {
                script {
                    container("maven") {
                        nodeInfo("mvn")
                    }
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    container("maven") {
                        withMavenPlus(
                            timestamps: true,
                            mavenLocalRepo: workspace().getM2LocalRepoPath(),
                            mavenSettingsConfig: '67aaee2b-ca74-4ae1-8eb9-c8f16eb5e534',
                            publisherStrategy: 'EXPLICIT'
                        ) {
                            sh "mvn clean install"
                        }
                    }
                }
            }
        }
    }
}
