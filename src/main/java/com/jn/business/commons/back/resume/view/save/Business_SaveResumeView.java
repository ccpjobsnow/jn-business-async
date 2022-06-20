package com.jn.business.commons.back.resume.view.save;

import com.ccp.decorators.CcpMapDecorator;
import com.ccp.dependency.injection.CcpEspecification;
import com.ccp.especifications.mensageria.consumer.CcpMensageriaMessageConsumer;
import com.jn.commons.JnBusinessEntity;
import com.jn.commons.JnTopic;

public class Business_SaveResumeView {

	@CcpEspecification
	private CcpMensageriaMessageConsumer messageConsumer;

	public void saveResumeView(CcpMapDecorator data) {
		
		CcpMapDecorator resumeDownload = this.messageConsumer.onConsumeMessage(data);
		
		CcpMapDecorator resumeDownloadShortData = resumeDownload.getSubMap(JnTopic.SAVE_RESUME_VIEW.fields);

		boolean alreadyViewed = JnBusinessEntity.view_resume.exists(resumeDownloadShortData);
		
		if(alreadyViewed) {
			JnBusinessEntity.duplicated_view_resume.save(resumeDownloadShortData);
			return;
		}

		String professional = resumeDownload.getAsString("professional");
		
		CcpMapDecorator professionalData = JnBusinessEntity.professional.get(professional);
		
		CcpMapDecorator viewResume = professionalData.getSubMap("ddd", "chatId", "jobType", "keywords", "deficient",
				"seniority", "homeoffice", "canChangeHouse", "canChangeSalary").putAll(resumeDownloadShortData);

		JnBusinessEntity.cache_view_resume.save(viewResume);
		JnBusinessEntity.view_resume.save(viewResume);
	
	}

}
