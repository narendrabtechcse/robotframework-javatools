/*
 * Copyright 2008 Nokia Siemens Networks Oyj
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.robotframework.jvmconnector.client;

import org.robotframework.javalib.library.RobotJavaLibrary;
import org.robotframework.jvmconnector.common.PropertyOverrider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Robot java library for remote keyword invocation.
 * Additional properties for the
 * org.robotframework.javalib.library.RobotJavaLibrary implementation
 * contained by RobotRmiService can be provided as a constructor argument in a
 * string. Use the following format:
 * <code>someProperty=someValue|anotherProperty=anotherValue</code>. The robot
 * java library implementation must have the corresponding
 * <code>setSomeProperty</code> and <code>setAnotherProperty</code> methods.
 */
public class RobotRemoteLibrary implements RobotJavaLibrary {
    private RobotJavaLibrary robotLibraryClient;
    private String properties;
    boolean reset = false;

    private String uri;

    
    public RobotRemoteLibrary() {
        this("rmi://localhost:1099/jvmConnector", null);
    }

    public RobotRemoteLibrary(String uri) {
        this(uri, null);
    }
    
    /**
     * @param properties
     *            property values in a string. Use the following format:
     *            <code>someProperty=someValue|anotherProperty=anotherValue</code>.
     */
    public RobotRemoteLibrary(String uri, String properties) {
        this.uri = uri;
        robotLibraryClient = createRobotLibraryClient();
    }
    
    public String[] getKeywordNames() {
        return robotLibraryClient.getKeywordNames();
    }

    public Object runKeyword(String keywordName, Object[] args) {
        return robotLibraryClient.runKeyword(keywordName, args);
    }

    RobotJavaLibrary createRobotLibraryClient() {
        ConfigurableListableBeanFactory beanFactory = createBeanFactory();
        overrideRmiURL(beanFactory, uri);
        return new RobotRmiClient(beanFactory, properties);
    }

    PropertyOverrider createPropertyOverrider() {
        return new PropertyOverrider();
    }

    ConfigurableListableBeanFactory createBeanFactory() {
        return new XmlBeanFactory(new ClassPathResource("org/robotframework/jvmconnector/client/clientContext.xml"));
    }

    private void overrideRmiURL(ConfigurableListableBeanFactory beanFactory, String rmiURL) {
        PropertyOverrider propertyOverrider = createPropertyOverrider();
        propertyOverrider.addOverridableProperty("robotRmiService.serviceUrl", rmiURL);
        propertyOverrider.postProcessBeanFactory(beanFactory);
    }
}
