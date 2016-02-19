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

import java.io.Closeable;
import java.net.InetAddress;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.OperationBuilder;
import org.jboss.dmr.ModelNode;

import static org.jboss.as.controller.client.helpers.ClientConstants.*;

public class DataSourceManager {

    public DataSourceManager() {
    }

    public List<ModelNode> getDataSources() throws Exception {
        final ModelNode request = new ModelNode();
        request.get( OP ).set( "read-resource" );
        request.get( "recursive" ).set( true );
        request.get( OP_ADDR ).add( "subsystem", "datasources" );
        ModelControllerClient client = null;

        try {
            client = createControllerClient();
            final ModelNode response = client.execute( new OperationBuilder( request ).build() );
            return response.get( RESULT ).get( "data-source" ).asList();
        } finally {
            safeClose( client );
        }
    }

    /**
     * @param name Seems to not be used any more in EAP 6.4.6 (pool-name attribute is used to hold the name value)
     * @param jndi (required) Specifies the JNDI name for the datasource.
     * @param connectionURL (required) The JDBC driver connection URL.
     * @param driverClass The fully qualified name of the JDBC driver class. (seems to be mandatory en EAP 6.4.6)
     * @param datasourceClass The fully qualified name of the JDBC datasource class.
     * @param driverName (required) Defines the JDBC driver the datasource should use. It is a symbolic name matching the the
     * name of installed driver. In case the driver is deployed as jar, the name is the name of deployment unit.
     * @param user
     * @param password
     * @param poolName Seems like EAP 6.4.6 uses the name as the pool name.
     * @param enabled Still in use in EAP 6.4.6 but deprecated for future versions.
     * Specifies if the datasource should be enabled. Note this attribute will not be supported runtime in next versions.
     * @throws Exception
     */
    public void createDatasource( String name,
            String jndi,
            String connectionURL,
            String driverClass,
            String datasourceClass,
            String driverName,
            String user,
            String password,
            String poolName,
            boolean enabled ) throws Exception {

        ModelNode operation = new ModelNode();
        operation.get( OP ).set( ADD );
        operation.get( OP_ADDR ).add( "subsystem", "datasources" );

        if ( name != null ) {
            //Seems to be no longer used in EAP 6.4.6.
            // The name entered in the management console goes directly to the pool-name attribute
            operation.get( OP_ADDR ).add( "data-source", name );
        }
        if ( jndi != null ) {
            operation.get( JBossDataSource.JNDI_NAME ).set( jndi );
        }
        if ( connectionURL != null ) {
            operation.get( JBossDataSource.CONNECTION_URL ).set( connectionURL );
        }
        if ( driverName != null ) {
            operation.get( JBossDataSource.DRIVER_NAME ).set( driverName );
        }
        if ( driverClass != null ) {
            operation.get( JBossDataSource.DRIVER_CLASS ).set( driverClass );
        }
        if ( datasourceClass != null ) {
            operation.get( JBossDataSource.DATASOURCE_CLASS ).set( datasourceClass );
        }
        if ( user != null ) {
            operation.get( JBossDataSource.USER_NAME ).set( user );
        }
        if ( password != null ) {
            operation.get( JBossDataSource.PASSWORD ).set( password );
        }
        if ( poolName != null ) {
            // not need to be set on EAP 6.4.6, it will be automatically set with the name
            //    request.get( "pool-name" ).set( poolName );
        }

        /*
        if ( enabled ) {
            //deprecated, won't be present in future versions.
            operation.get( "enabled" ).set( enabled );
        }
        */

        ModelControllerClient client = createControllerClient();
        ModelNode response = client.execute( new OperationBuilder( operation ).build() );

        safeClose( client );
        checkResponse( response );
    }

    private String getErrorDescription( ModelNode response ) {

        if ( response.hasDefined( FAILURE_DESCRIPTION ) ) {
            return response.get( FAILURE_DESCRIPTION ).asString();
        } else {
            return response.asString();
        }
    }

    public void updateDatasource( String name, Map<String, Object> changeSet ) throws Exception {

        //The operation is a composite operation of multiple attribute changes.
        ModelNode operation = new ModelNode();
        operation.get( OP ).set( COMPOSITE );
        operation.get( OP_ADDR ).setEmptyList();

        //Use a template for copying the datasource address
        ModelNode stepTemplate = new ModelNode();
        stepTemplate.get( OP ).set( "write-attribute" );
        stepTemplate.get( OP_ADDR ).add( "subsystem", "datasources" );
        stepTemplate.get( OP_ADDR ).add( "data-source", name );

        ModelNode step;
        ModelNode stepValue;
        List<ModelNode> steps = new ArrayList<ModelNode>();
        Object value;

        for ( String attrName : changeSet.keySet() ) {

            value = changeSet.get( attrName );
            if ( value == null ) {
                continue;
            }
            step = stepTemplate.clone();
            step.get( NAME ).set( attrName );
            stepValue = step.get( "value" );
            //TODO, this works fine for String attributes
            //should be improved to support other types admited by the ModelNode
            //by now it's ok to assume strings.
            stepValue.set( value.toString() );

            steps.add( step );
        }

        operation.get( STEPS ).set( steps );

        ModelControllerClient client = createControllerClient();
        ModelNode response = client.execute( new OperationBuilder( operation ).build() );

        safeClose( client );
        checkResponse( response );
    }

    public void enableDatasource( String name, boolean enable ) throws Exception {

        //-> /subsystem=datasources/data-source=ds2

        final String opName = enable ? "enable" : "disable";

        ModelNode operation = new ModelNode( );
        operation.get( OP ).set( opName );
        operation.get( OP_ADDR ).add( "subsystem", "datasources");
        operation.get( OP_ADDR ).add( "data-source", name );

        if ( ! enable  ) {
            operation.get( OPERATION_HEADERS ).get( "allow-resource-service-restart" ).set( true );
        }

        ModelControllerClient client = createControllerClient();
        ModelNode result = client.execute( operation );
        safeClose( client );
        checkResponse( result );

    }

    public void deleteDatasource( String name ) throws Exception {

        ModelNode operation = new ModelNode( );
        operation.get( OP ).set( "remove" );
        operation.get( OP_ADDR ).add( "subsystem", "datasources" );
        operation.get( OP_ADDR ).add( "data-source", name );

        ModelControllerClient client = createControllerClient();
        ModelNode result = client.execute( operation );
        safeClose( client );
        checkResponse( result );
    }


    public ModelControllerClient createControllerClient() throws Exception {
        return ModelControllerClient.Factory.create( InetAddress.getByName( "127.0.0.1" ), 9999 );
    }

    public void safeClose( final Closeable closeable ) {
        if ( closeable != null ) {
            try {
                closeable.close();
            } catch ( Exception e ) {

            }
        }
    }

    public void checkResponse( ModelNode response ) throws Exception {
        if ( "failed".equals( response.get( OUTCOME ) ) ) {
            throw new Exception( getErrorDescription( response ) );
        } else if ( "canceled".equals( response.get( OUTCOME ) ) ) {
            //to nothing
        } else if ( SUCCESS.equals( response.get( OUTCOME ) ) ) {
            //great!!!
        }
    }

    public void verifyDataSource( String jndi ) {
        try {

            InitialContext context = new InitialContext();
            DataSource ds = ( DataSource ) context.lookup( jndi );
            if ( ds == null ) {
                log( "Reference to datasource ds: " + jndi + " couldn't be obtained " );
            } else {
                log( "Reference to datasource ds: " + jndi + " was sucessfull obtained: " + ds );

                Connection conn = ds.getConnection();
                conn.close();
            }

        } catch ( Exception e ) {
            log( e.getMessage() );
            e.printStackTrace();
        }

    }

    public void log( String message ) {
        System.out.println( message );
    }


}
