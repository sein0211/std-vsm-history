def label = "worker-${env.JOB_NAME}-${env.BUILD_NUMBER}"
podTemplate(label: label,
  containers: [
    containerTemplate(name: 'gradle', image: 'gradle:8-jdk17-alpine', , command: 'cat', ttyEnabled: true)
  ]) {
  node(label) {
    properties([
      pipelineTriggers([
        [$class: 'GenericTrigger',
          genericVariables: [
            [key: 'user_name', value: '$.user_name'],
            [key: 'checkout_sha', value: '$.checkout_sha'],
            [key: 'ref', value: '$.ref'],
            [key: 'tag', value: '$.ref', regexpFilter: 'refs/tags/'],
            [key: 'event', value: '$.event_name']
          ], causeString: '$ref-$user_name:$checkout_sha', token: "std-vsm-history",
          printContributedVariables:false, printPostContent: false, silentResponse: true,
          regexpFilterText: '$ref', regexpFilterExpression: 'refs/heads/' + BRANCH_NAME
        ]
      ]),
      office365ConnectorWebhooks([[
          name: 'Teams',
          startNotification: true,
          notifySuccess: true,
          notifyAborted: false,
          notifyNotBuilt: false,
          notifyUnstable: true,
          notifyFailure: true,
          notifyBackToNormal: true,
          notifyRepeatedFailure: false,
          url: 'https://hkmccloud.webhook.office.com/webhookb2/e0ffc84f-ed7e-417e-b346-d1aeff8a9e30@f85ca5f1-aa23-4252-a83a-443d333b1fe7/IncomingWebhook/50b507fd1945402bbf0ffbb48a103601/97c2e140-49dd-45b7-a30b-e85bebdd2a21'
      ]])
    ])

    def repo = checkout scm
    def commit = repo.GIT_COMMIT
    def branch = repo.GIT_BRANCH
    def short_commit = "${commit[0..10]}"

    stage("Uploading Artifact"){
      container('gradle') {
        sh "gradle publish -x test"
      }
    }

    stage("Baking Docker"){
      container('gradle') {
        sh "gradle jib -x test"
      }
    }

    stage("Publishing Chart"){
      container('gradle') {
        if(branch == "develop" || branch == "release" || branch == "rc") {
            sh "gradle publishChart"
        }
      }
    }
  }
}