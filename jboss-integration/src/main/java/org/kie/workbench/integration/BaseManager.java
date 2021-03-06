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

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;

import static org.jboss.as.controller.client.helpers.ClientConstants.*;

public class BaseManager {

    public ModelControllerClient createControllerClient() throws Exception {
        return ModelControllerClient.Factory.create( InetAddress.getByName( "127.0.0.1" ), 9999 );
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

    public boolean isFailure( ModelNode response ) {
        return "failed".equals( response.get( OUTCOME ) );
    }

    public void safeClose( final Closeable closeable ) {
        if ( closeable != null ) {
            try {
                closeable.close();
            } catch ( Exception e ) {

            }
        }
    }

    private String getErrorDescription( ModelNode response ) {

        if ( response.hasDefined( FAILURE_DESCRIPTION ) ) {
            return response.get( FAILURE_DESCRIPTION ).asString();
        } else {
            return response.asString();
        }
    }

    public void log( String message ) {
        System.out.println( message );
    }
}
