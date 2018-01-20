package com.github.kiyohitonara.digestbot;


import java.io.*;
import java.net.URL;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class UploadObjectSingleOperation {
	private static String bucketName     = System.getenv("S3_BUCKETNAME");
    private static String s3_url     = System.getenv("S3_URL");
	//public String uploadFileName;
    public static String imageurl ;
	
	public void upfile(String uploadFileName) throws IOException {
        //System.out.println("ID:" + System.getenv("AWS_ACCESSKEY_ID") + "KEY:" + System.getenv("AWS_SECRET_ACCESSKEY"));
        //BasicAWSCredentials awsCreds = new BasicAWSCredentials(System.getenv("AWS_ACCESSKEY_ID"), System.getenv("AWS_SECRET_ACCESSKEY"));
        //AmazonS3 s3client = AmazonS3ClientBuilder.standard()
        //                        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
        //                        .build();
        //AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
        AmazonS3 s3client = new AmazonS3Client(
        new BasicAWSCredentials(
                System.getenv("AWS_ACCESSKEY_ID"),
                System.getenv("AWS_SECRET_ACCESSKEY")));

        try {
            System.out.println("Uploading a new object to S3 from a file\n");
            File file = new File("/tmp/video/" + uploadFileName);
            PutObjectRequest por = new PutObjectRequest(
                                     bucketName, uploadFileName, file);
            por.setCannedAcl(CannedAccessControlList.PublicRead);  
            s3client.putObject(por);

         } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
            		"means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    public static void upload(String userID,String filepath) throws IOException {

        AmazonS3 s3client = new AmazonS3Client( new BasicAWSCredentials(
                System.getenv("AWS_ACCESSKEY_ID"),
                System.getenv("AWS_SECRET_ACCESSKEY")));
        try {

            System.out.println("Uploading a new object to S3 from a file\n");
            String uploadFileName = "/tmp/"+DigestBotController.getJSTtime()+".jpg";
            File file = new File("/tmp/"+userID+".jpg");
            System.out.println("FILE SET");
            PutObjectRequest por = new PutObjectRequest(
                    bucketName, uploadFileName, file);
            por.setCannedAcl(CannedAccessControlList.PublicRead);
            s3client.putObject(por);

            //DBにつっこむURLの作成（原始的）
            imageurl=s3_url+uploadFileName;


        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());

        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }

    }

    private static void pipe(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        int len = 0;

        while ((len = in.read(buf)) > 0)
            out.write(buf, 0, len);
    }
    public static String getimageURL(){
        return imageurl;
    }
}