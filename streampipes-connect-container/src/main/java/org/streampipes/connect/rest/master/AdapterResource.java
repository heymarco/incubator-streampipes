/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.rest.master;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.config.ConnectContainerConfig;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.management.AdapterDeserializer;
import org.streampipes.connect.management.master.AdapterMasterManagement;
import org.streampipes.connect.rest.AbstractContainerResource;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.connect.adapter.*;
import org.streampipes.rest.shared.annotation.JsonLdSerialized;
import org.streampipes.rest.shared.util.JsonLdUtils;
import org.streampipes.rest.shared.util.SpMediaType;
import org.streampipes.storage.couchdb.impl.AdapterStorageImpl;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/v1/{username}/master/adapters")
public class AdapterResource extends AbstractContainerResource {

    private Logger logger = LoggerFactory.getLogger(AdapterResource.class);

    private AdapterMasterManagement adapterMasterManagement;

    private String connectContainerEndpoint;

    public AdapterResource() {
        this.adapterMasterManagement = new AdapterMasterManagement();
        this.connectContainerEndpoint = ConnectContainerConfig.INSTANCE.getConnectContainerUrl();
    }

    public AdapterResource(String connectContainerEndpoint) {
        this.adapterMasterManagement = new AdapterMasterManagement();
        this.connectContainerEndpoint = connectContainerEndpoint;
    }

    @POST
//    @JsonLdSerialized
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAdapter(String s, @PathParam("username") String userName) {

        AdapterDescription adapterDescription = null;


        try {
            adapterDescription = AdapterDeserializer.getAdapterDescription(s);
        } catch (AdapterException e) {
            logger.error("Could not deserialize AdapterDescription: " + s, e);
            e.printStackTrace();
        }
//        if (s.contains("GenericAdapterStreamDescription")) {
//            adapterDescription = JsonLdUtils.fromJsonLd(s, GenericAdapterStreamDescription.class);
//        } else if (s.contains("GenericAdapterSetDescription")){
//            adapterDescription = JsonLdUtils.fromJsonLd(s, GenericAdapterSetDescription.class);
//        }

        String newUrl = addUserNameToApi(connectContainerEndpoint, userName);

        try {
            adapterMasterManagement.addAdapter(adapterDescription, newUrl, new AdapterStorageImpl());
        } catch (AdapterException e) {
            logger.error("Error while starting adapter with id " + adapterDescription.getUri(), e);
            return ok(Notifications.error(e.getMessage()));
        }

        String responseMessage = "Stream adapter with id " + adapterDescription.getUri() + " successfully added";

        logger.info(responseMessage);
        return ok(Notifications.success(responseMessage));
    }

    @GET
    @JsonLdSerialized
    @Path("/{id}")
    @Produces(SpMediaType.JSONLD)
    public Response getAdapter(@PathParam("id") String id, @PathParam("username") String userName) {

        try {
            AdapterDescription adapterDescription = adapterMasterManagement.getAdapter(id, new AdapterStorageImpl());

            return ok(adapterDescription);
        } catch (AdapterException e) {
            logger.error("Error while getting adapter with id " + id, e);
            return fail();
        }

    }

    @DELETE
    @JsonLdSerialized
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAdapter(@PathParam("id") String id, @PathParam("username") String userName) {

        try {
            adapterMasterManagement.deleteAdapter(id);
            return ok(true);
        } catch (AdapterException e) {
            logger.error("Error while deleting adapter with id " + id, e);
            return fail();
        }
    }

    @GET
    @JsonLdSerialized
    @Path("/")
    @Produces(SpMediaType.JSONLD)
    public Response getAllAdapters(String id, @PathParam("username") String userName) {
        try {
            List<AdapterDescription> allAdapterDescription = adapterMasterManagement.getAllAdapters(new AdapterStorageImpl());
            AdapterDescriptionList result = new AdapterDescriptionList();
            result.setList(allAdapterDescription);

            return ok(result);
        } catch (AdapterException e) {
            logger.error("Error while getting all adapters", e);
            return fail();
        }

    }

    public void setAdapterMasterManagement(AdapterMasterManagement adapterMasterManagement) {
        this.adapterMasterManagement = adapterMasterManagement;
    }

    private String addUserNameToApi(String url, String userName) {
        return  url + "api/v1/" + userName + "/";
    }
}