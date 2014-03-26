package com.smart.plugin.mail.send.impl;

import com.smart.plugin.mail.send.AbstractMailSender;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;

public class HtmlMailSender extends AbstractMailSender {

    public HtmlMailSender(String subject, String content, String[] to) {
        super(subject, content, to);
    }

    @Override
    protected Email createEmail() {
        return new ImageHtmlEmail();
    }

    @Override
    protected void setContent(Email email, String content) throws MalformedURLException, EmailException {
        ImageHtmlEmail imageHtmlEmail = (ImageHtmlEmail) email;
        imageHtmlEmail.setDataSourceResolver(new DataSourceUrlResolver(new URL("http://"), true));
        imageHtmlEmail.setHtmlMsg(content);
    }
}
