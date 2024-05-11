// Define your secret project token here
def project_token = 'abcdefghijklmnopqrstuvwxyz0123456789ABCDEF'

// Reference the GitLab connection name from your Jenkins Global configuration (https://JENKINS_URL/configure, GitLab section)
properties([
    gitLabConnection('your-gitlab-connection-name'),
    pipelineTriggers([
        [
            $class: 'GitLabPushTrigger',
            branchFilterType: 'All',
            triggerOnPush: true,
            triggerOnMergeRequest: true,
            triggerOpenMergeRequestOnPush: "never",
            triggerOnNoteRequest: true,
            noteRegex: "Jenkins please retry a build",
            skipWorkInProgressMergeRequest: true,
            secretToken: project_token,
            ciSkip: false,
            setBuildDescription: true,
            addNoteOnMergeRequest: true,
            addCiMessage: true,
            addVoteOnMergeRequest: true,
            acceptMergeRequestOnSuccess: true,
            branchFilterType: "NameBasedFilter",
            includeBranchesSpec: "",
            excludeBranchesSpec: "",
        ]
    ])
])


node {
    try {

        def buildNum = env.BUILD_NUMBER
        def branchName= env.BRANCH_NAME
        print buildNum
        print branchName			

        stage('Env - clone generator'){
        	git 'https://github.com/jihedjarry/ayadi.git'
        }
	/* Run Build */
    	stage('SERVICE - Run'){
      		sh 'docker-compose up -d'
    	}
	/* Test */
    	stage('SERVICE - Run'){
      		sh 'curl localhost:4200'
    	}
		
    } finally {
        cleanWs()
    }
}
