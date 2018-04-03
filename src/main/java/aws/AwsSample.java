package aws;


import java.io.IOException;
/*
 * Copyright 2010 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * Modified by Sambit Sahu
 * Modified by Kyung-Hwa Kim (kk2515@columbia.edu)
 * 
 * 
 */
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;



public class AwsSample {

	/*
	 * Important: Be sure to fill in your AWS access credentials in the
	 * AwsCredentials.properties file before you try to run this sample.
	 * http://aws.amazon.com/security-credentials
	 */

	static AmazonEC2 ec2;
	static AWSCredentials credentials;
	static String groupId = "sg-64ebf500";
	static String securityGroupName = "launch-wizard-1";
	static String keyName = "rason_test";
	static String imageId = "ami-d8578bb5"; // Basic 32-bit Amazon Linux AMI
	static String createdInstanceId = null;
	
	public static AmazonEC2Client Ec2authInit () throws IOException {
				credentials = new PropertiesCredentials(
				AwsSample.class.getResourceAsStream("AwsCredentials.properties"));
				
				System.out.println(credentials.getAWSAccessKeyId()+"======"+credentials.getAWSSecretKey());
				System.out.println("#1 Create Amazon Client object");
				
				ec2 = new AmazonEC2Client(credentials);
				Region region = Region.getRegion(Regions.fromName("cn-north-1"));
				ec2.setRegion(region);
				return (AmazonEC2Client) ec2;
	}
	
	public static void createEc2Instance(String instanceType,int minInstanceCount, int maxInstanceCount ) {
		System.out.println("#4.3 Create an Instance");
		
		RunInstancesRequest rir = new RunInstancesRequest();
		rir.withImageId(imageId).withInstanceType(instanceType).withMinCount(minInstanceCount)
				.withMaxCount(maxInstanceCount).withKeyName(keyName).withSecurityGroups(securityGroupName);
		RunInstancesResult result = ec2.runInstances(rir);

		// get instanceId from the result
		List<Instance> resultInstance = result.getReservation().getInstances();
		
		for (Instance ins : resultInstance) {
			createdInstanceId = ins.getInstanceId();
			System.out.println("New instance has been created: " + ins.getInstanceId());
			try{
				System.out.println("waiting for a while ....");
				Thread.sleep(1000*60);	
				System.out.println("finished for a while ....");
			}catch(Exception e){
				
			}
		}
		
	}
	
	public static void createEc2TagForInstance() {
		System.out.println("#5 Create a 'tag' for the new instance.");
		List<String> resources = new LinkedList<String>();
		List<Tag> tags = new LinkedList<Tag>();
		Tag nameTag = new Tag("infinitus_test", "MyInstance"+Math.random()*1000);
		resources.add(createdInstanceId);
		tags.add(nameTag);
		CreateTagsRequest ctr = new CreateTagsRequest(resources, tags);
		ec2.createTags(ctr);
	}

	public static Set<Instance> getEc2InstanceList(){
		System.out.println("#9 Describe Current Instances");
		DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
		List<Reservation> reservations = describeInstancesRequest.getReservations();
		Set<Instance> instances = new HashSet<Instance>();
		// add all instances to a Set.
		for (Reservation reservation : reservations) {
			instances.addAll(reservation.getInstances());
		}

		System.out.println("You have " + instances.size() + " Amazon EC2 instance(s).");
		for (Instance ins : instances) {

			// instance id
			String instanceId = ins.getInstanceId();
			// instance state
			InstanceState is = ins.getState();
			
			System.out.println(instanceId + " " + is.getName());
			//System.out.println("Public IP Address: " + ins.getPublicIpAddress() + "\nPrivate IP Address: " + ins.getPrivateIpAddress());
		}
		return instances;
	} 
	
	public static void createRdsInstance() throws IOException {
		
		credentials = new PropertiesCredentials(
				AwsSample.class.getResourceAsStream("AwsCredentials.properties"));
				AmazonRDSClient rds = new AmazonRDSClient(credentials);
				
				Region region = Region.getRegion(Regions.fromName("cn-north-1"));
				rds.setRegion(region);
				System.out.println(rds);
				
				//createDBInstance(CreateDBInstanceRequest request)
				CreateDBInstanceRequest request = new CreateDBInstanceRequest();
				request.setAllocatedStorage(50);
				request.setLicenseModel("general-public-license");
				request.setEngine("mysql");
				request.setEngineVersion("5.7.16");
				request.setMultiAZ(true);
				request.setDBInstanceClass("db.t2.micro");
				request.setStorageType("standard");
				request.setDBInstanceIdentifier("test1");
				request.setMasterUsername("mysqlInfinitus");
				request.setMasterUserPassword("Infinitus2018!");
				Collection c = new ArrayList();
				c.add(groupId);
				request.setVpcSecurityGroupIds(c);
				request.setPubliclyAccessible(true);
				request.setDBName("testApi");
				request.setPort(3306);
				//request.dbpa(dBParameterGroupName);
				rds.createDBInstance(request);
				System.out.println("rds 创建成功");

	}
	public static void main(String[] args) throws Exception {

//		ec2 = Ec2authInit();
//		createEc2Instance( "t2.small", 1, 1);			
//		createEc2TagForInstance();
//		getEc2InstanceList();.
//		createRdsInstance();
		
		
		
		try {
			

		} catch (AmazonServiceException ase) {
			System.out.println("Caught Exception: " + ase.getMessage());
			System.out.println("Reponse Status Code: " + ase.getStatusCode());
			System.out.println("Error Code: " + ase.getErrorCode());
			System.out.println("Request ID: " + ase.getRequestId());
		}

	}
}
