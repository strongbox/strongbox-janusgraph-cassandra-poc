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
                    contains("maven") {
                        sh "mvn clean install"
                    }
                }
            }
        }
    }
}
