node {
  // Mark the code checkout 'stage'....
  stage 'Stage Checkout'

  // Checkout code from repository and update any submodules
  checkout scm
  sh 'git submodule update --init'  

  stage 'Stage Build'

  //branch name from Jenkins environment variables
  echo "My branch is: ${env.BRANCH_NAME}"

  // runs all the tests
  sh "./gradlew cleanTest test"
  // run checkstyle
  sh "./gradlew checkstyle"

//  def flavor = flavor(env.BRANCH_NAME)
//  echo "Building flavor ${flavor}"
//
//  //build your gradle flavor, passes the current build number as a parameter to gradle
//  sh "./gradlew clean assemble${flavor}Debug -PBUILD_NUMBER=${env.BUILD_NUMBER}"
  sh "./gradlew clean assembleDebug -PBUILD_NUMBER=${env.BUILD_NUMBER}"

  stage 'Report'
//    step([$class: 'JUnitResultArchiver', testResults: 'gitlist-PHP/build/logs/junit.xml'])
    step([$class: 'hudson.plugins.checkstyle.CheckStylePublisher', checkstyle: 'app/build/reports/checkstyle/checkstyle.html'])
//    step([$class: 'hudson.plugins.dry.DryPublisher', CopyPasteDetector: 'gitlist-PHP/build/logs/phpcpd.xml'])

  stage 'Stage Archive'
  //tell Jenkins to archive the apks
  step([$class: 'ArtifactArchiver', artifacts: 'app/build/outputs/apk/*.apk', fingerprint: true])

//  stage 'Stage Upload To Fabric'
//  sh "./gradlew crashlyticsUploadDistribution${flavor}Debug  -PBUILD_NUMBER=${env.BUILD_NUMBER}"
}

// Pulls the android flavor out of the branch name the branch is prepended with /QA_
@NonCPS
def flavor(branchName) {
  def matcher = (env.BRANCH_NAME =~ /QA_([a-z_]+)/)
  assert matcher.matches()
  matcher[0][1]
}
