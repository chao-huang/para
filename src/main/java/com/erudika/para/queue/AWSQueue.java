/*
 * Copyright 2013 Alex Bogdanovski <alex@erudika.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You can reach the author at: https://github.com/albogdano
 */
package com.erudika.para.queue;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.erudika.para.utils.Config;
import com.erudika.para.utils.Utils;
import java.util.List;
import org.slf4j.Logger;
import javax.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Alex Bogdanovski <alex@erudika.com>
 */
@Singleton
public class AWSQueue implements Queue {
	
	private static final int MAX_MESSAGES = 1;  //max in bulk
	private AmazonSQSAsyncClient sqs;
	private static final String SQS_ENDPOINT = "https://sqs.".concat(Config.AWS_REGION).concat(".amazonaws.com");
	private static final Logger logger = LoggerFactory.getLogger(AWSQueue.class);
	private ObjectMapper mapper = Utils.getInstance().getObjectMapper();
	private String name;
	private String endpoint;
	private String url;

	public AWSQueue() {
		this("test123", SQS_ENDPOINT);
	}
	
	// This queue contains only messages in JSON format!
	public AWSQueue(String name, String endpoint){
		sqs = new AmazonSQSAsyncClient(new BasicAWSCredentials(Config.AWS_ACCESSKEY, Config.AWS_SECRETKEY));
		if (StringUtils.isBlank(endpoint)) {
			sqs.setEndpoint(SQS_ENDPOINT, name, Config.AWS_REGION);
			this.endpoint = SQS_ENDPOINT;
		} else {
			sqs.setEndpoint(endpoint, name, Config.AWS_REGION);
			this.endpoint = endpoint;
		}
		this.name = name;
		url = create(name);
	}

	public void push(String task) {
		if(!StringUtils.isBlank(url) && task != null){
			// only allow strings - ie JSON
			if (!StringUtils.isBlank(task)) {
				// Send a message
				try {
					SendMessageRequest sendReq = new SendMessageRequest();
					sendReq.setQueueUrl(url);					
					sendReq.setMessageBody(task);
					
					sqs.sendMessage(sendReq);
				} catch (AmazonServiceException ase) {
					logException(ase);
				} catch (AmazonClientException ace) {
					logger.error("Could not reach SQS. {}", ace.getMessage());
				}
			}
		}
	}

	public String pull() {
		String task = "[]";
		if(!StringUtils.isBlank(url)){
			try {
				ReceiveMessageRequest receiveReq = new ReceiveMessageRequest(url);
				receiveReq.setMaxNumberOfMessages(MAX_MESSAGES);
				List<Message> list = sqs.receiveMessage(receiveReq).getMessages();
				
				if (list != null && !list.isEmpty()) {
					Message message = list.get(0);
					sqs.deleteMessage(new DeleteMessageRequest(url, message.getReceiptHandle()));
					task = message.getBody();
				}
			} catch (AmazonServiceException ase) {
				logException(ase);
			} catch (AmazonClientException ace) {
				logger.error("Could not reach SQS. {}", ace.getMessage());
			}
		}
		return task;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	private String create(String name){
		String u = endpoint.concat("/").concat(name);
		try{
			u = sqs.createQueue(new CreateQueueRequest(name)).getQueueUrl();
		} catch (AmazonServiceException ase) {
			logException(ase);
		} catch (AmazonClientException ace) {
			logger.error("Could not reach SQS. {0}", ace.getMessage());
		}	
		return u;
	}
//	
//	public void delete(){
//		try {
//			sqs.deleteQueue(new DeleteQueueRequest(QUEUE_URL));
//		} catch (AmazonServiceException ase) {
//			logException(ase);
//		} catch (AmazonClientException ace) {
//			logger.error("Could not reach SQS. {0}", ace.getMessage());
//		}
//	}
//	
//	public List<String> listQueues(){
//		List<String> list = null;
//		try {
//			list = sqs.listQueues().getQueueUrls();
//		} catch (AmazonServiceException ase) {
//			logException(ase);
//		} catch (AmazonClientException ace) {
//			logger.error("Could not reach SQS. {0}", ace.getMessage());
//		}
//		return list;
//	}
	
	private void logException(AmazonServiceException ase){
		logger.error("AmazonServiceException: error={0}, statuscode={1}, "
			+ "awserrcode={2}, errtype={3}, reqid={4}", ase.getMessage(), ase.getStatusCode(), 
			ase.getErrorCode(), ase.getErrorType(), ase.getRequestId());
	}
}