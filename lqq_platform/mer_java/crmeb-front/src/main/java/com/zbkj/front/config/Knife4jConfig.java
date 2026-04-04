package com.zbkj.front.config;


import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.github.xiaoymin.knife4j.core.util.CollectionUtils.newArrayList;

/**
 * 类的详细说明
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/7/24
 */
@Configuration
@EnableKnife4j
@EnableSwagger2WebMvc
public class Knife4jConfig {

    //是否开启swagger，正式环境一般是需要关闭的，可根据springboot的多环境配置进行设置
    @Value("${knife4j.enable}")
    Boolean swaggerEnabled;

    @Autowired
    CrmebConfig crmebConfig;

    // 自定义分割符
    private static final String splitor = ";";

    @Bean("afront")
    public Docket create1RestApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("afront")
                .host(crmebConfig.getDomain())
                .apiInfo(apiInfo())
                // 是否开启
                .enable(swaggerEnabled)
                .select()
                // 扫描的路径包
//                .apis(RequestHandlerSelectors.basePackage("com.zbkj.front"))
                // 此种配置方法填写需要的包
                .apis(basePackage("com.zbkj.front.controller.staff" + splitor + "com.zbkj.front.controller.employee"))
                // 指定路径处理PathSelectors.any()代表所有的路径
                .paths(frontPathsAnt()) //只监听
                .build()
                .securitySchemes(security())
                .securityContexts(securityContexts())
//                .globalOperationParameters(pars) // 针对单个url的验证 如果需要的话
                .pathMapping("/");
    }

    @Bean("employee")
    public Docket create2RestApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("employee")
                .host(crmebConfig.getDomain())
                .apiInfo(apiInfo())
                // 是否开启
                .enable(swaggerEnabled)
                .select()
                // 扫描的路径包
                .apis(RequestHandlerSelectors.basePackage("com.zbkj.front.controller.employee"))
                // 指定路径处理PathSelectors.any()代表所有的路径
                .paths(employeePathsAnt()) //只监听
                .build()
                .securitySchemes(security())
                .securityContexts(securityContexts())
//                .globalOperationParameters(pars) // 针对单个url的验证 如果需要的话
                .pathMapping("/");
    }

    @Bean("staff")
    public Docket create3RestApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("staff")
                .host(crmebConfig.getDomain())
                .apiInfo(apiInfo())
                // 是否开启
                .enable(swaggerEnabled)
                .select()
                // 扫描的路径包
                .apis(RequestHandlerSelectors.basePackage("com.zbkj.front.controller.staff"))
                // 指定路径处理PathSelectors.any()代表所有的路径
                .paths(staffPathsAnt()) //只监听
                .build()
                .securitySchemes(security())
                .securityContexts(securityContexts())
//                .globalOperationParameters(pars) // 针对单个url的验证 如果需要的话
                .pathMapping("/");
    }

    private Predicate<String> frontPathsAnt() {
        return PathSelectors.ant("/api/front/**");
    }

    private Predicate<String> employeePathsAnt() {
        return PathSelectors.ant("/api/front/employee/**");
    }

    private Predicate<String> staffPathsAnt() {
        return PathSelectors.ant("/api/front/staff/**");
    }

    private List<ApiKey> security() {
        return newArrayList(
                new ApiKey(Constants.HEADER_AUTHORIZATION_KEY, Constants.HEADER_AUTHORIZATION_KEY, "header")
        );
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("Crmeb","https://www.crmeb.com/index/java_merchant", "278437628@qq.com");
        return new ApiInfoBuilder()
                .title("Crmeb Java 多商户用户侧")
                .description("Java多商户接口文档")
                .contact(contact)
                .termsOfServiceUrl("https://www.crmeb.com/index/java_merchant")
                .version("1.4.0").build();
    }


    private List<SecurityContext> securityContexts() {
        List<SecurityContext> res = new ArrayList<>();
        res.add(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/.*"))
                .build());
        return res;
    }

    private List<SecurityReference> defaultAuth() {
        List<SecurityReference> res = new ArrayList<>();
        AuthorizationScope authorizationScope = new AuthorizationScope("global", Constants.HEADER_AUTHORIZATION_KEY);
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        res.add(new SecurityReference(Constants.HEADER_AUTHORIZATION_KEY, authorizationScopes));
        return res;
    }

    /**
     * 重写basePackage方法，使能够实现多包访问
     * 排除掉不需要的包
     */
    public static Predicate<RequestHandler> basePackage(final String basePackage) {
        return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
    }

    private static Function<Class<?>, Boolean> handlerPackage(final String basePackage)     {
        return input -> {
            // 循环判断匹配
            for (String strPackage : basePackage.split(splitor)) {
                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                if (isMatch) {
                    return false;
                }
            }
            return true;
        };
    }

    private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.fromNullable(input.declaringClass());
    }

}
