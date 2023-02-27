pipeline {
    agent any

    stages {
        
    
        stage('Checking NODE version and NPM') {
            steps {
                sh "node -v"
                sh "npm -v"
            }
        }
        
        stage('Checkout'){
            steps{
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: '04bab23d-6247-42cd-a135-155a3e34f5b9', url: 'https://github.com/anshuman0053/postman-newman.git']])
            }
        }
        
        stage('Build Xray token python script'){
            steps{
                git branch: 'main', credentialsId: '04bab23d-6247-42cd-a135-155a3e34f5b9', url: 'https://github.com/anshuman0053/postman-newman.git'
                //sh 'python3 postman_upload.py'
                
                script{
                        def token = sh(script: "python3 postman_upload.py", returnStdout: true).trim()
                        env.BEARER_TOKEN = token
                        
                }
                sh "echo $BEARER_TOKEN" 
            }
        }
        
        stage('Running Postman API Collection via Newman'){
            steps{
                git branch: 'main', credentialsId: '04bab23d-6247-42cd-a135-155a3e34f5b9', url: 'https://github.com/anshuman0053/postman-newman.git'
                script{
                        def newman_aut = sh(script: "python3 parallel-execution-newman.py", returnStdout: true).trim()
                        
                }
                  
            }
        }
        stage('Uploading Newman reports to Xray JIRA'){
            steps{
                sh "pwd"
                sh 'curl -H "Content-Type: text/xml" -X POST -H "Authorization: $BEARER_TOKEN" --data @newman_junitfull.xml https://xray.cloud.getxray.app/api/v2/import/execution/junit?projectKey=CQ'
            }
        }
        
        stage('Test Pipeline'){
            steps{
                echo "Pipeline Tested Successuly"
            }
        }
    }
}

