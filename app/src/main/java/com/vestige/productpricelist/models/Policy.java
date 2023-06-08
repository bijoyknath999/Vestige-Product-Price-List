package com.vestige.productpricelist.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Policy {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("privacy_policy")
    @Expose
    private String privacyPolicy;
    @SerializedName("terms_conditions")
    @Expose
    private String termsConditions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrivacyPolicy() {
        return privacyPolicy;
    }

    public void setPrivacyPolicy(String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }

    public String getTermsConditions() {
        return termsConditions;
    }

    public void setTermsConditions(String termsConditions) {
        this.termsConditions = termsConditions;
    }

}