package com.github.walterfan.devaid.webmonitor;

import java.io.*;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

public class HttpUploader {

	private String destUrl_;

	private String srcFileName_;

	public HttpUploader(String fileUrl, String destFile) {
		this.destUrl_ = fileUrl;
		this.srcFileName_ = destFile;
	}

	public static void main(String[] args) {
		String destUrl = "http://10.224.71.155/receiver.do";
		String srcFile = "";

		if (args.length > 1) {
			destUrl = args[0];
			srcFile = args[1];
		} else {
			System.exit(1);
		}
		System.out.println("Upload " + srcFile + " to " + destUrl);
		HttpUploader uploader = new HttpUploader(destUrl, srcFile);
		uploader.upload();
	}

	public int upload() {
		File f = new File(this.srcFileName_);
		PostMethod filePoster = new PostMethod(this.destUrl_);
		int status = -1;
		HttpClient client = new HttpClient();
		try {
			Part[] parts = { new FilePart(f.getName(), f) };
			filePoster.setRequestEntity(new MultipartRequestEntity(parts,
					filePoster.getParams()));
			status = client.executeMethod(filePoster);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			filePoster.releaseConnection();
		}
		return status;
	}

	

}
