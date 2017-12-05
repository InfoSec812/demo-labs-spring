# Innovation Labs Security & Compliance Demo

## Overview
This project demonstrates some of the features we can provide in 
a Labs residency around security and compliance analysis in a
DevOps environment.
* OWASP Zed Attack Proxy
* OWASP Dependency Check
* SonarQube Static Analysis

## Prerquisites
* Ansible (>= 2.4)
* Ansible-Galaxy
* OpenShift Container Platform CLI (oc)
* An OpenShift cluster where this demo can be deployed (OCP >= 3.6)

## Deploying this demo

* This demo takes some time to prepare/deploy
* This demo cannot be deployed **LIVE** during a talk

1. Check out this repository
2. Install [casl-ansible](https://github.com/redhat-cop/casl-ansible) using Ansible Galaxy
   * `ansible-galaxy install --roles-path . -r requirements.yml`
3. Log on to an OpenShift cluster where the demo will be run
   * `oc login -u <username> <OCP Master URL>`
4. Deploy the application and CI/CD environment to OpenShift
   * `ansible-playbook --connection=local -i inventory casl-ansible/playbooks/openshift-cluster-seed.yml`
  
## Running this demo

1. When the demo is first deployed, the CI/CD environment will be deployed
   * Could take as long as 30 minutes
2. Once the CI/CD environment is deployed, the Spring application will be built
   * Manual approval to deploy to the `Demo` environment will be required
3. The initial build of the application WILL fail
   * This is intentional... The initial build has insecure dependencies and insecure web semantics
4. The first step is to show the failed build and show the reasons for the failed build
   * Log in to Jenkins, display the OWASP Dependency Check Report
5. Fix the dependency check problem by switching to the `Step_1` branch
6. Trigger the pipeline to rebuild
7. Show that the build failed because of the ZAP scan finding insecure practices in the web app
8. Fix the ZAP scan issue by switching to the `Step_2` branch
9. Trigger the pipeline to rebuild
10. Show that the build now passes both scans
