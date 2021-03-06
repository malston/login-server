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
import org.cloudfoundry.identity.uaa.login.test.IfProfileActive;
import org.cloudfoundry.identity.uaa.login.test.IntegrationTestRule;
import org.cloudfoundry.identity.uaa.login.test.LoginServerClassRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

@RunWith(LoginServerClassRunner.class)
@ContextConfiguration(classes = DefaultIntegrationTestConfig.class)
@IfProfileActive("saml")
public class SamlLoginIT {

    @Autowired @Rule
    public IntegrationTestRule integrationTestRule;

    @Autowired
    WebDriver webDriver;

    @Value("${integration.test.base_url}")
    String baseUrl;

    @Test
    public void testSamlVariations() throws Exception {
        webDriver.get(baseUrl + "/login");
        Assert.assertEquals("Cloud Foundry", webDriver.getTitle());

        webDriver.findElement(By.name("username"));
        webDriver.findElement(By.name("password"));
        webDriver.findElement(By.xpath("//a[text()='Use your corporate credentials']"));
        webDriver.findElement(By.xpath("//input[@value='Sign in']"));
        Assert.assertEquals(3, webDriver.findElements(By.xpath("//input")).size());
    }
}
