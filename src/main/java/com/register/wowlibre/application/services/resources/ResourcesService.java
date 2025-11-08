package com.register.wowlibre.application.services.resources;


import com.register.wowlibre.domain.dto.faqs.CreateFaqDto;
import com.register.wowlibre.domain.enums.FaqType;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.model.resources.*;
import com.register.wowlibre.domain.port.in.ResourcesPort;
import com.register.wowlibre.domain.port.out.JsonLoaderPort;
import com.register.wowlibre.domain.port.out.faqs.ObtainFaqs;
import com.register.wowlibre.domain.port.out.faqs.SaveFaqs;
import com.register.wowlibre.infrastructure.entities.FaqsEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourcesService implements ResourcesPort {
    private final JsonLoaderPort jsonLoaderPort;
    private final ObtainFaqs obtainFaqs;
    private final SaveFaqs saveFaqs;

    public ResourcesService(JsonLoaderPort jsonLoaderPort, ObtainFaqs obtainFaqs, SaveFaqs saveFaqs) {
        this.jsonLoaderPort = jsonLoaderPort;
        this.obtainFaqs = obtainFaqs;
        this.saveFaqs = saveFaqs;
    }

    @Override
    public List<CountryModel> getCountry(String transactionId) {
        return jsonLoaderPort.getJsonCountry(transactionId);
    }

    @Override
    public List<FaqsModel> getFaqs(FaqType type, String language, String transactionId) {
        return obtainFaqs.findByTypeAndLanguage(type, language).stream()
                .map(faqsEntity -> new FaqsModel(faqsEntity.getId(), faqsEntity.getQuestion(),
                        faqsEntity.getAnswer(), faqsEntity.getType())).toList();
    }

    @Override
    public void createFaq(CreateFaqDto createFaq, String transactionId) {
        FaqType faqType = FaqType.getByName(createFaq.getType());

        if (faqType == null) {
            throw new InternalException("Invalid FAQ type: ", transactionId);
        }

        FaqsEntity faqsEntity = new FaqsEntity();
        faqsEntity.setQuestion(createFaq.getQuestion());
        faqsEntity.setAnswer(createFaq.getAnswer());
        faqsEntity.setType(faqType);
        faqsEntity.setLanguage(createFaq.getLanguage());
        saveFaqs.save(faqsEntity, transactionId);
    }

    @Override
    public void deleteFaq(Long faqId, String transactionId) {
        saveFaqs.delete(faqId, transactionId);
    }


    @Override
    public List<PlanModel> getPlansBank(String language, String transactionId) {
        return jsonLoaderPort.getJsonPlans(language, transactionId);
    }

    @Override
    public List<BenefitModel> getBenefitsGuild(String language, String transactionId) {
        return jsonLoaderPort.getJsonBenefitsGuild(language, transactionId);
    }


    @Override
    public WidgetHomeSubscriptionModel getWidgetSubscription(String language, String transactionId) {
        return jsonLoaderPort.getWidgetSubscription(language, transactionId);
    }


}
