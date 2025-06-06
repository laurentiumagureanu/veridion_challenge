package com.veridion.api.rabbitmq.dto;

public record CompanySearchRequest(String name, String phone, String website, String socialMediaLink) {
  public boolean isEmpty() {
    return (name == null || name.isBlank())
        && (phone == null || phone.isBlank())
        && (website == null || website.isBlank())
        && (socialMediaLink == null || socialMediaLink.isBlank());
  }
}
