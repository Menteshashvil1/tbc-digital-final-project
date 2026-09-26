package ge.tbc.testautomation.api.clients;

import ge.tbc.testautomation.utils.ConfigReader;
import ge.tbc.testautomation.utils.JsonMapper;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public abstract class BaseApiClient {
    private static final RestAssuredConfig CONFIG = RestAssured.config()
            .objectMapperConfig(new ObjectMapperConfig()
                    .jackson2ObjectMapperFactory((Jackson2ObjectMapperFactory) (type, charset) -> JsonMapper.mapper()));

    private final RequestSpecification specification = new RequestSpecBuilder()
            .setBaseUri(ConfigReader.apiUrl())
            .setAccept(ContentType.JSON)
            .setConfig(CONFIG)
            .addFilter(new AllureRestAssured())
            .log(LogDetail.URI)
            .build();

    protected RequestSpecification request() {
        return given().spec(specification);
    }
}
