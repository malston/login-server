/*******************************************************************************
 *     Cloud Foundry 
 *     Copyright (c) [2009-2014] Pivotal Software, Inc. All Rights Reserved.
 *
 *     This product is licensed to you under the Apache License, Version 2.0 (the "License").
 *     You may not use this product except in compliance with the License.
 *
 *     This product includes a number of subcomponents with
 *     separate copyright notices and license terms. Your use of these
 *     subcomponents is subject to the terms and conditions of the
 *     subcomponent's license, as noted in the LICENSE file.
 *******************************************************************************/
package org.cloudfoundry.identity.uaa.login.feature;

import org.cloudfoundry.identity.uaa.login.test.DefaultIntegrationTestConfig;
import org.cloudfoundry.identity.uaa.login.test.IntegrationTestRule;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.test.TestAccounts;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DefaultIntegrationTestConfig.class)
public class LoginIT {

    @Autowired @Rule
    public IntegrationTestRule integrationTestRule;

    @Autowired
    WebDriver webDriver;

    @Value("${integration.test.base_url}")
    String baseUrl;
    
    @Autowired
    TestAccounts testAccounts;

    @Test
    public void testLoggingIn() throws Exception {
        webDriver.get(baseUrl + "/login");
        Assert.assertEquals("Cloud Foundry", webDriver.getTitle());

        webDriver.findElement(By.name("username")).sendKeys(testAccounts.getUserName());
        webDriver.findElement(By.name("password")).sendKeys(testAccounts.getPassword());
        webDriver.findElement(By.xpath("//input[@value='Sign in']")).click();

        Assert.assertThat(webDriver.findElement(By.cssSelector("h1")).getText(), Matchers.containsString("Where to?"));
    }

    @Test
    public void testLoggingInFailure() throws Exception {
        webDriver.get(baseUrl + "/login");
        Assert.assertEquals("Cloud Foundry", webDriver.getTitle());

        webDriver.findElement(By.name("username")).sendKeys(testAccounts.getUserName());
        webDriver.findElement(By.name("password")).sendKeys("invalidpassword");
        webDriver.findElement(By.xpath("//input[@value='Sign in']")).click();

        Assert.assertThat(webDriver.findElement(By.cssSelector("h1")).getText(), Matchers.containsString("Welcome!"));
    }

    @Test
    public void testFailedLogin() throws Exception {
        RestTemplate template = new RestTemplate();
        LinkedMultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("username", testAccounts.getUserName());
        body.add("password", "invalidpassword");
        ResponseEntity<Void> loginResponse = template.exchange(baseUrl + "/login.do",
            HttpMethod.POST,
            new HttpEntity<>(body, null),
            Void.class);
        Assert.assertEquals(HttpStatus.FOUND, loginResponse.getStatusCode());

    }

    @Test
    public void testBuildInfo() throws Exception {
        webDriver.get(baseUrl + "/login");

        String regex = "Version: \\S+, Commit: \\w{7}, Timestamp: \\S+, UAA: http://localhost:8080/uaa";
        Assert.assertTrue(webDriver.findElement(By.cssSelector(".footer .copyright")).getAttribute("title").matches(regex));
    }
}
