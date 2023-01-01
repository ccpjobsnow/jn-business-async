package com.ccp.jn.async;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.jn.async.business.NotifyContactUs;
import com.ccp.jn.async.business.RequestTokenAgain;
import com.ccp.jn.async.business.RequestUnlockToken;
import com.ccp.jn.async.business.SaveCandidateData;
import com.ccp.jn.async.business.SaveResumesQuery;
import com.ccp.jn.async.business.SendEmail;
import com.ccp.jn.async.business.SendInstantMessage;
import com.ccp.jn.async.business.SendUserToken;
import com.jn.commons.JnBusinessTopic;

public interface AsyncServices {
	
	CcpMapDecorator catalog = new CcpMapDecorator()
			
			.put(JnBusinessTopic.requestUnlockToken.name(), new RequestUnlockToken())
			.put(JnBusinessTopic.sendInstantMessage.name(), new SendInstantMessage())
			.put(JnBusinessTopic.saveCandidateData.name(), new SaveCandidateData())
			.put(JnBusinessTopic.requestTokenAgain.name(), new RequestTokenAgain())
			.put(JnBusinessTopic.saveResumesQuery.name(), new SaveResumesQuery())
			.put(JnBusinessTopic.notifyContactUs.name(), new NotifyContactUs())
			.put(JnBusinessTopic.sendUserToken.name(), new SendUserToken())
			.put(JnBusinessTopic.sendEmail.name(), new SendEmail())
			;
}
