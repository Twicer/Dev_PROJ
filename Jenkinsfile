pipeline {
  agent {
    node {
      label 'instance1'
    }

  }
  stages {
    stage('Environment') {
      parallel {
        stage('Environment') {
          steps {
            sh '''sudo apt update
sudo apt search openjdk sudo apt -y install openjdk-9-jdk sudo apt-cache search maven sudo apt -y install maven'''
            sh 'sudo apt install default-jre'
            sh '''sudo apt install default-jdk
'''
          }
        }

        stage('error') {
          steps {
            sh '#mvn -B verify --file pom.xml install'
          }
        }

      }
    }

    stage('Test') {
      steps {
        sh 'echo "Wohoo"'
      }
    }

    stage('Package') {
      steps {
        sh 'mvn package'
      }
    }

    stage('Build Maven') {
      steps {
        sh '''cd wise-webapp

mvn -U jetty:run-war'''
        sh 'mvn jetty:run-war'
      }
    }

    stage('Connection Test') {
      steps {
        sh '''
curl 0.0.0.0:8080
ss -tulpn
'''
      }
    }

  }
}