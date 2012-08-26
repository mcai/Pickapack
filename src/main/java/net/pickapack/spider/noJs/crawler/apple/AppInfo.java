package net.pickapack.spider.noJs.crawler.apple;

public class AppInfo {
    private String appExtVrsId;
    private String price;
    private String pricingParameters;
    private String productType;
    private String salableAdamId;

    public AppInfo(String appExtVrsId, String price, String pricingParameters, String productType, String salableAdamId) {
        this.appExtVrsId = appExtVrsId;
        this.price = price;
        this.pricingParameters = pricingParameters;
        this.productType = productType;
        this.salableAdamId = salableAdamId;
    }

    public String getAppExtVrsId() {
        return appExtVrsId;
    }

    public String getPrice() {
        return price;
    }

    public String getPricingParameters() {
        return pricingParameters;
    }

    public String getProductType() {
        return productType;
    }

    public String getSalableAdamId() {
        return salableAdamId;
    }
}
