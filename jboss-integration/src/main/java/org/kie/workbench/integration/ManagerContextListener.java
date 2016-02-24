/*
 * Copyright 2016 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.integration;

import java.util.Date;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ManagerContextListener implements ServletContextListener {

    public void contextInitialized( ServletContextEvent servletContextEvent ) {
        System.out.println( "XXXXXXXXXXX ManagerApplication about to be initialized: " + new Date() );
    }

    public void contextDestroyed( ServletContextEvent servletContextEvent ) {
        System.out.println( "YYYYYYYYYYY ManagerApplication about to be destroyed: " + new Date() );
    }
}
