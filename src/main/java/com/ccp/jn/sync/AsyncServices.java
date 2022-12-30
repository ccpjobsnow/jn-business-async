package com.ccp.jn.sync;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.jn.sync.business.NotifyContactUs;
import com.ccp.jn.sync.business.RequestTokenAgain;
import com.ccp.jn.sync.business.RequestUnlockToken;
import com.ccp.jn.sync.business.SaveCandidateData;
import com.ccp.jn.sync.business.SaveResumesQuery;
import com.ccp.jn.sync.business.SendUserToken;
import com.jn.commons.JnBusinessTopic;

public interface AsyncServices {
	
	CcpMapDecorator catalog = new CcpMapDecorator()
			
			.put(JnBusinessTopic.requestUnlockToken.name(), new RequestUnlockToken())
			.put(JnBusinessTopic.saveCandidateData.name(), new SaveCandidateData())
			.put(JnBusinessTopic.requestTokenAgain.name(), new RequestTokenAgain())
			.put(JnBusinessTopic.saveResumesQuery.name(), new SaveResumesQuery())
			.put(JnBusinessTopic.notifyContactUs.name(), new NotifyContactUs())
			.put(JnBusinessTopic.sendUserToken.name(), new SendUserToken())
			;
}
