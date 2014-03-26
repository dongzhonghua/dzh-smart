package com.smart.plugin.mail.send.impl;

import com.smart.plugin.mail.send.AbstractMailSender;
import java.net.MalformedURLException;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

public class TextMailSender extends AbstractMailSender {

    public TextMailSender(String subject, String content, String[] to) {
        super(subject, content, to);
    }

    @Override
    protected Email createEmail() {
        return new MultiPartEmail();
    }

    @Override
    protected void setContent(Email email, String content) throws MalformedURLException, EmailException {
        email.setMsg(content);
    }
}
