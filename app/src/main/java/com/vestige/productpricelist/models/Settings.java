package com.vestige.productpricelist.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Settings {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("versionCode")
    @Expose
    private String versionCode;
    @SerializedName("adsStatus")
    @Expose
    private String adsStatus;
    @SerializedName("adsType")
    @Expose
    private String adsType;
    @SerializedName("interstitialAdsCount")
    @Expose
    private String interstitialAdsCount;
    @SerializedName("adsLimit")
    @Expose
    private String adsLimit;
    @SerializedName("bannerAdmob")
    @Expose
    private String bannerAdmob;
    @SerializedName("interstitialAdmob")
    @Expose
    private String interstitialAdmob;
    @SerializedName("nativeAdmob")
    @Expose
    private String nativeAdmob;
    @SerializedName("appOpenAdmob")
    @Expose
    private String appOpenAdmob;
    @SerializedName("rewardAdmob")
    @Expose
    private String rewardAdmob;
    @SerializedName("bannerFacebook")
    @Expose
    private String bannerFacebook;
    @SerializedName("interstitialFacebook")
    @Expose
    private String interstitialFacebook;
    @SerializedName("rewardFacebook")
    @Expose
    private String rewardFacebook;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getAdsStatus() {
        return adsStatus;
    }

    public void setAdsStatus(String adsStatus) {
        this.adsStatus = adsStatus;
    }

    public String getAdsType() {
        return adsType;
    }

    public void setAdsType(String adsType) {
        this.adsType = adsType;
    }

    public String getInterstitialAdsCount() {
        return interstitialAdsCount;
    }

    public void setInterstitialAdsCount(String interstitialAdsCount) {
        this.interstitialAdsCount = interstitialAdsCount;
    }

    public String getAdsLimit() {
        return adsLimit;
    }

    public void setAdsLimit(String adsLimit) {
        this.adsLimit = adsLimit;
    }

    public String getBannerAdmob() {
        return bannerAdmob;
    }

    public void setBannerAdmob(String bannerAdmob) {
        this.bannerAdmob = bannerAdmob;
    }

    public String getInterstitialAdmob() {
        return interstitialAdmob;
    }

    public void setInterstitialAdmob(String interstitialAdmob) {
        this.interstitialAdmob = interstitialAdmob;
    }

    public String getNativeAdmob() {
        return nativeAdmob;
    }

    public void setNativeAdmob(String nativeAdmob) {
        this.nativeAdmob = nativeAdmob;
    }

    public String getAppOpenAdmob() {
        return appOpenAdmob;
    }

    public void setAppOpenAdmob(String appOpenAdmob) {
        this.appOpenAdmob = appOpenAdmob;
    }

    public String getRewardAdmob() {
        return rewardAdmob;
    }

    public void setRewardAdmob(String rewardAdmob) {
        this.rewardAdmob = rewardAdmob;
    }

    public String getBannerFacebook() {
        return bannerFacebook;
    }

    public void setBannerFacebook(String bannerFacebook) {
        this.bannerFacebook = bannerFacebook;
    }

    public String getInterstitialFacebook() {
        return interstitialFacebook;
    }

    public void setInterstitialFacebook(String interstitialFacebook) {
        this.interstitialFacebook = interstitialFacebook;
    }

    public String getRewardFacebook() {
        return rewardFacebook;
    }

    public void setRewardFacebook(String rewardFacebook) {
        this.rewardFacebook = rewardFacebook;
    }

}