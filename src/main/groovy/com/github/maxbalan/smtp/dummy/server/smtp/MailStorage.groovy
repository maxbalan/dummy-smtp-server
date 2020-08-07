package com.github.maxbalan.smtp.dummy.server.smtp

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.tuple.ImmutableTriple
import org.apache.commons.lang3.tuple.Triple
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.ConcurrentHashMap

/**
 * 02/06/18
 *
 * @author maxbalan
 * */
class MailStorage {

    private final Logger LOG = LoggerFactory.getLogger(MailStorage.class)

    private final ConcurrentHashMap<String, ConcurrentHashMap<Long, Triple<String, String, String>>> storage

    public MailStorage() {
        storage = new ConcurrentHashMap<>()
    }

    public void saveEmail(String from, String to, InputStream data) {
        LOG.info("Received a new Email from [ $from ] to [ $to ]")

        //convert input stream to a readable string
        def content = IOUtils.toString(data, "UTF-8")
        //store the email data in memory
        def email = new ImmutableTriple(from, to, content)

        if (this.storage.containsKey(to)) {
            def s = this.storage.get(to)
            s.put(System.nanoTime(), email)
            this.storage.put(to, s)
        } else {
            def s = new ConcurrentHashMap<Long, Triple<String, String, String>>()
            s.put(System.nanoTime(), email)
            this.storage.put(to, s)
        }

        LOG.info("Stored datta TO [ $to ] >> Email [ $email ]")
    }

    public def getEmailByRecipient(String recipient) {
        this.storage.get(recipient)
    }

}
