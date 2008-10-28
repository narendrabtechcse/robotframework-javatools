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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.robotframework.javalib.library.RobotJavaLibrary;
import org.robotframework.javalib.util.KeywordNameNormalizer;
import org.robotframework.jvmconnector.common.PropertyOverrider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;


/**
 * <p>
 * Robot java library for remote keyword invocation.
 * </p>
 * <p>
 * Additional properties for the
 * org.robotframework.javalib.library.RobotJavaLibrary implementation
 * contained by RobotRmiService can be provided as a constructor argument in a
 * string. Use the following format:
 * <code>someProperty=someValue|anotherProperty=anotherValue</code>. The robot
 * java library implementation must have the corresponding
 * <code>setSomeProperty</code> and <code>setAnotherProperty</code> methods.
 * </p>
 * 
 * @author Heikki Hulkko
 */
public class RobotRemoteLibrary implements RobotJavaLibrary {
    static final String RESET = "resetrobotlibraryclient";
    
    private RobotJavaLibrary robotLibraryClient;
    private final String host;
    private final String port;
    private final String properties;
    boolean reset = false;
    
    public RobotRemoteLibrary() {
        this("localhost", "1099", null);
    }

    public RobotRemoteLibrary(String host, String port) {
        this(host, port, null);
    }

    /**
     * @param properties
     *            property values in a string. Use the following format:
     *            <code>someProperty=someValue|anotherProperty=anotherValue</code>.
     */
    public RobotRemoteLibrary(String host, String port, String properties) {
        this.host = host;
        this.port = port;
        this.properties = properties;
        robotLibraryClient = createRobotLibraryClient();
    }

    public String[] getKeywordNames() {
        List keywordsAsList = getKeywordNamesAsList();
        keywordsAsList.add(RESET);
        return (String[]) keywordsAsList.toArray(new String[0]);
    }

    public Object runKeyword(String keywordName, Object[] args) {
        resetLibraryIfNecessary();
        if (new KeywordNameNormalizer().normalize(keywordName).equals(RESET)) {
            reset = true;
            return Boolean.TRUE;
        }
        return robotLibraryClient.runKeyword(keywordName, args);
    }

    RobotJavaLibrary createRobotLibraryClient() {
        ConfigurableListableBeanFactory beanFactory = createBeanFactory();
        overrideRmiURL(beanFactory, "rmi://" + host + ":" + port + "/jvmConnector");
        return new RobotRmiClient(beanFactory, properties);
    }

    PropertyOverrider createPropertyOverrider() {
        return new PropertyOverrider();
    }

    ConfigurableListableBeanFactory createBeanFactory() {
        return new XmlBeanFactory(new ClassPathResource("org/robotframework/jvmconnector/client/clientContext.xml"));
    }

    private void resetLibraryIfNecessary() {
        if (reset) {
            robotLibraryClient = createRobotLibraryClient();
            reset = false;
        }
    }

    private void overrideRmiURL(ConfigurableListableBeanFactory beanFactory, String rmiURL) {
        PropertyOverrider propertyOverrider = createPropertyOverrider();
        propertyOverrider.addOverridableProperty("robotRmiService.serviceUrl", rmiURL);
        propertyOverrider.postProcessBeanFactory(beanFactory);
    }

    private ArrayList getKeywordNamesAsList() {
        return new ArrayList(Arrays.asList(robotLibraryClient.getKeywordNames()));
    }
}
