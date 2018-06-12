package org.apereo.cas.services;


import org.apereo.cas.config.MongoDbServiceRegistryConfiguration;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

import static org.junit.Assert.*;


/**
 * This is {@link MongoServiceRegistryCloudTests}.
 *
 * @author Misagh Moayyed
 * @since 4.2.0
 */
@SpringBootTest(classes = {MongoDbServiceRegistryConfiguration.class, RefreshAutoConfiguration.class})
@TestPropertySource(locations = {"classpath:/mongoservices.properties"})
@Slf4j
public class MongoServiceRegistryCloudTests {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    @Qualifier("mongoDbServiceRegistry")
    private ServiceRegistry serviceRegistry;


    @BeforeEach
    public void clean() {
        final var services = this.serviceRegistry.load();
        services.forEach(service -> this.serviceRegistry.delete(service));
    }

    @Test
    public void verifySaveAndLoad() {
        final List<RegisteredService> list = new ArrayList<>();
        IntStream.range(0, 5).forEach(i -> {
            list.add(buildService(i));
            this.serviceRegistry.save(list.get(i));
        });
        final var results = this.serviceRegistry.load();
        assertEquals(results.size(), list.size());
        IntStream.range(0, 5).forEach(i -> assertEquals(list.get(i), results.get(i)));
        IntStream.range(0, 5).forEach(i -> this.serviceRegistry.delete(results.get(i)));
        assertTrue(this.serviceRegistry.load().isEmpty());
    }

    @AfterEach
    public void after() {
        clean();
    }

    private static RegisteredService buildService(final int i) {
        final var rs = RegisteredServiceTestUtils.getRegisteredService("^http://www.serviceid" + i + ".org");

        final Map<String, RegisteredServiceProperty> propertyMap = new HashMap<>();
        final var property = new DefaultRegisteredServiceProperty();
        final Set<String> values = new HashSet<>();
        values.add("value10");
        values.add("value20");
        property.setValues(values);
        propertyMap.put("field2", property);
        rs.setProperties(propertyMap);
        rs.setUsernameAttributeProvider(new AnonymousRegisteredServiceUsernameAttributeProvider());
        
        return rs;
    }
}