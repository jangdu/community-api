package com.jangdu.community.global.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "cloud.aws.s3")
public class StorageProperties {

    private String bucket;
    private String region;
    private String accessKey;
    private String secretKey;
}
