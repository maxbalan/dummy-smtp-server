package com.github.maxbalan.smtp.dummy.server.resources

import com.github.maxbalan.smtp.dummy.server.smtp.MailStorage
import org.apache.commons.lang3.tuple.Triple
import org.json.JSONObject

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * 03/06/18
 *
 * @author maxbalan
 * */
@Path("email")
class EmailResource {

    private MailStorage mailStorage

    @GET
    public def getEmail(@QueryParam("recipient") String recipient) {
        def email = mailStorage.getEmailByRecipient(recipient)
        def responseJson = tripleToJson(email)

        Response.ok().entity(responseJson.toString()).type(MediaType.APPLICATION_JSON_TYPE).build()
    }

    private def tripleToJson(Triple email) {
        def json = new JSONObject()

        //early return in case there is no email
        if (email == null)
            return json

        json.put("from", email.getLeft())
        json.put("to", email.getMiddle())
        json.put("email", email.getRight())

        json
    }

}
