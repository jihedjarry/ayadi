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

    	/* Modification de la version dans le pom.xml */
    	sh "sed -i s/'-SNAPSHOT'/${extension}/g /spring-blog-backend/pom.xml"


	/* Récupération de la version du pom.xml après modification */
    	def version = sh returnStdout: true, script: "/spring-blog-backend/pom.xml | grep -A1 '<artifactId>myapp1' | tail -1 |perl -nle 'm{.*<version>(.*)</version>.*};print \$1' | tr -d '\n'"

     	print """
	#################################################
        BanchName: $branchName
        AppVersion: $version
	JobNumber: $buildNum
     	#################################################
        """

		

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
