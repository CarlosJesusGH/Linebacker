/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.linebacker.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.linebacker.backend.OfyService.ofy;

/**
 * An endpoint to send messages to ONE device registered with the backend
 *
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 *
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(
  name = "sendmessage",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "backend.linebacker.com",
    ownerName = "backend.linebacker.com",
    packagePath=""
  )
)
public class SendMessageEndpoint {
    private static final Logger log = Logger.getLogger(SendMessageEndpoint.class.getName());

    /** Api Keys can be obtained from the google cloud console */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    /**
     * Send to the first 10 devices (You can modify this to send to any number of devices or a specific device)
     *
     * @param message The message to send
     */
    public void sendMessage(@Named("message") String message, @Named("deviceId") String deviceId) throws IOException {
        if(message == null || message.trim().length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }
        // crop longer messages
        if (message.length() > 1000) {
            message = message.substring(0, 1000) + "[...]";
        }
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();
        //List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).limit(10).list();
        //for(RegistrationRecord record : records) {
            Result result = sender.send(msg, deviceId, 5);
            if (result.getMessageId() != null) {
                log.info("Message sent to " + deviceId);
                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    // if the regId changed, we have to update the datastore
                    //log.info("Registration Id changed for " + record.getRegId() + " updating to " + canonicalRegId);
                    //record.setRegId(canonicalRegId);
                    //ofy().save().entity(record).now();
                }
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    log.warning("Registration Id " + deviceId + " no longer registered with GCM, removing from datastore");
                    // if the device is no longer registered with Gcm, remove it from the datastore
                    //ofy().delete().entity(record).now();
                }
                else {
                    log.warning("Error when sending message : " + error);
                }
            }
        //}
    }
}