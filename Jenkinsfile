#!groovy​
node {
  // Mark the code checkout 'stage'....
  stage 'Stage Checkout'

  // Checkout code from repository and update any submodules
//  checkout scm
//  sh 'git submodule update --init'
  checkout([
          $class: 'GitSCM',
          branches: scm.branches,
          doGenerateSubmoduleConfigurations: true,
          extensions: scm.extensions + [[$class: 'SubmoduleOption', parentCredentials: true]],
          userRemoteConfigs: scm.userRemoteConfigs
  ])

  stage 'Stage Build'

  //branch name from Jenkins environment variables
  echo "My branch is: ${env.BRANCH_NAME}"

  // runs all the tests
  try {
    sh "./gradlew cleanTest test --no-daemon"
  } catch (err) {
    currentBuild.result = 'UNSTABLE'
  }
  stash includes: '**/test-results/**/*.xml', name: 'junit-reports'

  // run checkstyle
  sh "./gradlew checkstyle --no-daemon"
  stash includes: '**/reports/checkstyle/*.xml', name: 'checkstyle-reports'


//  def flavor = flavor(env.BRANCH_NAME)
//  echo "Building flavor ${flavor}"
//
//  //build your gradle flavor, passes the current build number as a parameter to gradle
//  sh "./gradlew clean assemble${flavor}Debug -PBUILD_NUMBER=${env.BUILD_NUMBER}"
  sh "./gradlew clean assembleDebug -PBUILD_NUMBER=${env.BUILD_NUMBER} --no-daemon"
}

stage 'Report'
node {
//    step([$class: 'CheckStylePublisher', pattern: '**/checkstyle-result.xml'])
  unstash 'checkstyle-reports'
  step([$class: 'hudson.plugins.checkstyle.CheckStylePublisher', checkstyle: 'app/build/reports/checkstyle/checkstyle-result.xml'])

  unstash 'junit-reports'
  step([$class: 'JUnitResultArchiver', testResults: '**/test-results/**/*.xml'])
}

stage 'Stage Archive'
node {
  //tell Jenkins to archive the apks
  step([$class: 'ArtifactArchiver', artifacts: 'app/build/outputs/apk/*/*/*.apk', fingerprint: true])
}

stage 'Stage Upload To Fabric'
node {
  if (env.BRANCH_NAME == 'develop') {
    sh "./gradlew crashlyticsUploadDistributionVimojoDebug  -PBUILD_NUMBER=${env.BUILD_NUMBER} --no-daemon"
    //sh "./gradlew crashlyticsUploadDistributionHispanopostDebug  -PBUILD_NUMBER=${env.BUILD_NUMBER} --no-daemon"
    //sh "./gradlew crashlyticsUploadDistributionRtveDebug  -PBUILD_NUMBER=${env.BUILD_NUMBER} --no-daemon"
    sh "./gradlew crashlyticsUploadDistributionThomsonFoundationDebug  -PBUILD_NUMBER=${env.BUILD_NUMBER} --no-daemon"
    //sh "./gradlew crashlyticsUploadDistributionVimojowatermarkDebug  -PBUILD_NUMBER=${env.BUILD_NUMBER} --no-daemon"
    //sh "./gradlew crashlyticsUploadDistributionNemSummitDebug  -PBUILD_NUMBER=${env.BUILD_NUMBER} --no-daemon"
    sh "./gradlew crashlyticsUploadDistributionM4NDebug  -PBUILD_NUMBER=${env.BUILD_NUMBER} --no-daemon"
  }
}

// Pulls the android flavor out of the branch name the branch is prepended with /QA_
@NonCPS
def flavor(branchName) {
  def matcher = (env.BRANCH_NAME =~ /QA_([a-z_]+)/)
  assert matcher.matches()
  matcher[0][1]
}
