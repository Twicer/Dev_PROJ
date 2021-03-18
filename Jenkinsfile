pipeline {
  agent {
    node {
      label 'LocalDebian10'
    }

  }
  stages {
    stage('Environment') {
      parallel {
        stage('Environment') {
          steps {
            sh '''apt update
apt searchopenjdk apt -y install openjdk-9-jdk apt-cache search maven apt -y install maven'''
          }
        }

        stage('') {
          steps {
            sh 'mvn -B verify --file pom.xml install'
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