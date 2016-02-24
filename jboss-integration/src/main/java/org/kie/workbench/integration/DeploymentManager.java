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

import java.util.ArrayList;
import java.util.List;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;

import static org.jboss.as.controller.client.helpers.ClientConstants.*;

public class DeploymentManager extends BaseManager {

    public void deployContent( String deploymentName, String runtimeName, byte[] content, boolean enabled ) throws Exception {

        ModelNode response = null;

        try {
            ModelControllerClient client = createControllerClient();

            ModelNode operation = new ModelNode();

            operation.get( OP ).set( ADD );
            operation.get( OP_ADDR ).add( DEPLOYMENT, deploymentName );

            List<ModelNode> contentList = new ArrayList<ModelNode>();
            ModelNode contentNode = new ModelNode();
            contentNode.set( "bytes", content );
            contentList.add( contentNode );

            operation.get( "name" ).set( deploymentName );
            operation.get( "content" ).set( contentList );
            operation.get( "enabled" ).set( enabled );
            operation.get( "runtime-name" ).set( runtimeName );

            response = client.execute( operation );

            safeClose( client );
        } finally {
            checkResponse( response );
        }
    }

}
