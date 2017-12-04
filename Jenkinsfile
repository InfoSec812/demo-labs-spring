#!/usr/bin/groovy

/**
 this section of the pipeline executes on the master, which has a lot of useful variables that we can leverage to configure our pipeline
 **/
node (''){
    // these should align to the projects in the Application Inventory
    env.NAMESPACE = env.OPENSHIFT_BUILD_NAMESPACE.reverse().drop(6).reverse()
    env.DEV_PROJECT = "${env.NAMESPACE}-dev"
    env.TEST_PROJECT = "${env.NAMESPACE}-demo"

    // this value should be set to the root directory of your source code within the git repository.
    // if the root of the source is the root of the repo, leave this value as ""
    env.SOURCE_CONTEXT_DIR = ""
    // this value is relative to env.SOURCE_CONTEXT_DIR, and should be set to location where mvn will build the uber-jar
    env.UBER_JAR_CONTEXT_DIR = "target/"

    /**
     these are used to configure which repository maven deploys
     the ci-cd starter will create a nexus that has this repos available
     **/
    env.MVN_SNAPSHOT_DEPLOYMENT_REPOSITORY = "nexus::default::http://nexus:8081/repository/maven-snapshots"
    env.MVN_RELEASE_DEPLOYMENT_REPOSITORY = "nexus::default::http://nexus:8081/repository/maven-releases"

    /**
     this value assumes the following convention, which is enforced by our default templates:
     - there are two build configs: one for s2i, one for this pipeline
     - the buildconfig for this pipeline is called my-app-name-pipeline
     - both buildconfigs are in the same project
     **/
    env.APP_NAME = "${env.JOB_NAME}".replaceAll(/-?${env.PROJECT_NAME}-?/, '').replaceAll(/-?pipeline-?/, '')

    // these are defaults that will help run openshift automation
    env.OCP_API_SERVER = "${env.OPENSHIFT_API_URL}"
    env.OCP_TOKEN = readFile('/var/run/secrets/kubernetes.io/serviceaccount/token').trim()
}


/**
 this section of the pipeline executes on a custom mvn build slave.
 you should not need to change anything below unless you need new stages or new integrations (e.g. Cucumber Reports or Sonar)
 **/
node('mvn-build-pod') {

    stage('SCM Checkout') {
        checkout scm
    }

    dir("${env.SOURCE_CONTEXT_DIR}") {
        stage('Build App') {
            // TODO - introduce a variable here
            sh "mvn org.jacoco:jacoco-maven-plugin:prepare-agent package org.jacoco:jacoco-maven-plugin:report"
            publishHTML([  // Publish JaCoCo Coverage Report
                           allowMissing: false,
                           alwaysLinkToLastBuild: false,
                           keepAll: false,
                           reportDir: 'target/site/jacoco',
                           reportFiles: 'index.html',
                           reportName: 'JaCoCo Test Coverage Report',
                           reportTitles: 'JaCoCo Test Coverage Report'
            ])
        }

        stage('Check dependencies') {
            sh "mvn dependency-check:check"
            publishHTML([  // Publish Dependency Check Report
                           allowMissing: false,
                           alwaysLinkToLastBuild: false,
                           keepAll: false,
                           reportDir: 'target/',
                           reportFiles: 'dependency-check-report.html',
                           reportName: 'Dependency Check Report',
                           reportTitles: 'Dependency Check Report'
            ])
        }

        stage('Perform Quality Analysis') {
            withSonarQubeEnv {
                sh "mvn sonar:sonar"
            }
        }

        // assumes uber jar is created
        stage('Build Image') {
            sh "oc start-build ${env.APP_NAME} --from-dir=${env.UBER_JAR_CONTEXT_DIR} --follow"
        }
    }
}

node('') {

    // no user changes should be needed below this point
    stage('Deploy to Dev') {
        input "Promote Application to Dev?"
        openshiftTag(apiURL: "${env.OCP_API_SERVER}", authToken: "${env.OCP_TOKEN}", destStream: "${env.APP_NAME}", destTag: 'latest', destinationAuthToken: "${env.OCP_TOKEN}", destinationNamespace: "${env.DEV_PROJECT}", namespace: "${env.OPENSHIFT_BUILD_NAMESPACE}", srcStream: "${env.APP_NAME}", srcTag: 'latest')
        openshiftVerifyDeployment(apiURL: "${env.OCP_API_SERVER}", authToken: "${env.OCP_TOKEN}", depCfg: "${env.APP_NAME}", namespace: "${env.DEV_PROJECT}", verifyReplicaCount: true)
    }
}

node('zap-build-pod') {
    stage('ZAP Scan') {
        def retVal = sh returnStatus: true, script: '/zap/zap-baseline.py -r baseline.html -t http://${env.DEV_PROJECT}/'
        publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: true, reportDir: '/zap/wrk', reportFiles: 'baseline.html', reportName: 'ZAP Baseline Scan', reportTitles: 'ZAP Baseline Scan'])
        echo "Return value is: ${retVal}"
    }
}

node('') {
    stage ('Deploy to Demo') {
        input "Promote Application to Demo environment?"
        openshiftTag (apiURL: "${env.OCP_API_SERVER}", authToken: "${env.OCP_TOKEN}", destStream: "${env.APP_NAME}", destTag: 'latest', destinationAuthToken: "${env.OCP_TOKEN}", destinationNamespace: "${env.TEST_PROJECT}", namespace: "${env.DEV_PROJECT}", srcStream: "${env.APP_NAME}", srcTag: 'latest')
        openshiftVerifyDeployment (apiURL: "${env.OCP_API_SERVER}", authToken: "${env.OCP_TOKEN}", depCfg: "${env.APP_NAME}", namespace: "${env.TEST_PROJECT}", verifyReplicaCount: true)
    }
}