package com.vestige.productpricelist.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("netContent")
    @Expose
    private String netContent;
    @SerializedName("productCode")
    @Expose
    private String productCode;
    @SerializedName("mrp")
    @Expose
    private String mrp;
    @SerializedName("dp")
    @Expose
    private String dp;
    @SerializedName("pv")
    @Expose
    private String pv;
    @SerializedName("categoryID")
    @Expose
    private String categoryID;
    @SerializedName("productImage")
    @Expose
    private String productImage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getNetContent() {
        return netContent;
    }

    public void setNetContent(String netContent) {
        this.netContent = netContent;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getPv() {
        return pv;
    }

    public void setPv(String pv) {
        this.pv = pv;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

}