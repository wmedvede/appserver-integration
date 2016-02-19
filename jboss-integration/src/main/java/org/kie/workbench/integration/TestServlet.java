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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.dmr.ModelNode;

@WebServlet( "/managerServlet" )
public class TestServlet extends HttpServlet {

    private DataSourceManager dsManager = new DataSourceManager();

    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        doProcess( request, response );
    }

    @Override
    protected void doPost( HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException {
        doProcess( req, resp );
    }

    protected void doProcess( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        try {

            String name = request.getParameter( "name" );
            String jndi = request.getParameter( "jndi" );
            String connectionURL = request.getParameter( "connectionURL" );
            String driverClass = request.getParameter( "driverClass" );
            String datasourceClass = request.getParameter( "datasourceClass" );
            String driverName = request.getParameter( "driverName" );
            String user = request.getParameter( "user" );
            String password = request.getParameter( "password" );
            String poolName = request.getParameter( "poolName" );
            boolean enabled = Boolean.TRUE.toString().equals( request.getParameter( "enabled" ) );

            String operation = request.getParameter( "operation" );
            if ( "list".equals( operation ) ) {
                printDataSouces( response );
            } else if ( "create".equals( operation ) ) {
                dsManager.createDatasource( name, jndi, connectionURL, driverClass, datasourceClass, driverName,
                        user, password, poolName, enabled );
            } else if ( "verify".equals( operation ) ) {
                dsManager.verifyDataSource( jndi );
            } else if ( "enable".equals( operation ) ) {
                dsManager.enableDatasource( name, enabled );
            } else if ( "delete".equals( operation ) ) {
                dsManager.deleteDatasource( name );
            } else if ( "update".equals( operation ) ) {
                Map<String, Object> changes = new HashMap<String, Object>(  );

                changes.put( JBossDataSource.JNDI_NAME, jndi );
                changes.put( JBossDataSource.CONNECTION_URL, connectionURL );
                changes.put( JBossDataSource.DRIVER_CLASS, driverClass );
                changes.put( JBossDataSource.DATASOURCE_CLASS, datasourceClass );
                changes.put( JBossDataSource.DRIVER_NAME, driverName );
                changes.put( JBossDataSource.USER_NAME, user );
                changes.put( JBossDataSource.PASSWORD, password );
                changes.put( JBossDataSource.POOL_NAME, poolName );
                dsManager.updateDatasource( name, changes );

            } else {
                response.getWriter().println( " Unknown operation: " + operation );
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void printDataSouces( HttpServletResponse response ) throws Exception {
        List<ModelNode> datasources = dsManager.getDataSources();
        for ( ModelNode node : datasources ) {
            response.getWriter().println( node.toString() );
        }
    }

        /*

    http://localhost:8080/jboss-integration-1.0-SNAPSHOT/Test?op=create&name=testds1&jndi=java:/comp/env/TestDS1&connectionURL=url&driverName=h2&driverClass=org.h2.Driver&driverName=driverName&user=test-user&password=test-password&poolName=the-pool-name

    http://localhost:8080/jboss-integration-1.0-SNAPSHOT/Test?op=create&name=testds1&jndi=java:/comp/env/TestDS1&connectionURL=url&driverName=h2&driverClass=org.h2.Driver&user=test-user&password=test-password&poolName=the-pool-name


    http://localhost:8080/jboss-integration-1.0-SNAPSHOT/Test?op=create&name=testds2&jndi=java:/comp/env/TestDS2&connectionURL=url&driverName=h2&driverClass=org.h2.Driver&user=test-user&password=test-password&poolName=the-pool-name2


    http://localhost:8080/jboss-integration-1.0-SNAPSHOT/Test?op=create&name=testds3&jndi=java:/comp/env/TestDS3&connectionURL=url&driverClass=org.h2.Driver&user=test-user&password=test-password&poolName=the-pool-name3



    org.h2.Driver

    */

    //http://www.mastertheboss.com/jboss-server/jboss-as-7/using-jboss-management-api-programmatically#


}
