pipeline {
    agent any
  
    parameters{

         string(defaultValue: 'upload-result-on-env', description: '', name: 'BRANCH', trim: true)
         string(defaultValue: 'https://api.postman.com/collections/18481634-926bcc97-e8db-4354-ac57-e293cd106587?access_key=PMAT-01GV2Y8JN6HSKEW0PRMRKK8GVA', description: '', name: 'COLLECTION_URL', trim: true)
        
                             
    }  
  
 
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
                // git branch: 'main', credentialsId: '04bab23d-6247-42cd-a135-155a3e34f5b9', url: 'https://github.com/anshuman0053/postman-newman.git'
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
                script{
                        // Define the command to run the script and pass any required parameters
                        def command = "python3 parallel-execution-newman.py '${params.COLLECTION_URL}'"

                        // Run the script using sh step and capture the output
                        def output = sh(script: command, returnStdout: true).trim()

                        echo output
                        
                }
                  
            }
        }
        
        
    }

    post{
        always{

            sh 'curl -H "Content-Type: text/xml" -X POST -H "Authorization: $BEARER_TOKEN" --data @report.xml https://xray.cloud.getxray.app/api/v2/import/execution/junit?projectKey=CQ'
        }
    }
}

