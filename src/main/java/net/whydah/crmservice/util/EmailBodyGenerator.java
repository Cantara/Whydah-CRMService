package net.whydah.crmservice.util;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;


/**
 */

public class EmailBodyGenerator {
	private static final Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_0);;

	private static final String EMAIL_TEMPLATE = "EmailVerification.ftl";

	static {
		try {
			File customTemplate = new File("./templates/email");
			FileTemplateLoader ftl = null;
			if (customTemplate.exists()) {
				ftl = new FileTemplateLoader(customTemplate);
			}
			ClassTemplateLoader ctl = new ClassTemplateLoader(EmailBodyGenerator.class, "/templates/email");

			TemplateLoader[] loaders = null;
			if (ftl != null) {
				loaders = new TemplateLoader[]{ftl, ctl};
			} else {
				loaders = new TemplateLoader[]{ctl};
			}

			MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);
			freemarkerConfig.setTemplateLoader(mtl);
			freemarkerConfig.setObjectWrapper(new DefaultObjectWrapper());
			freemarkerConfig.setDefaultEncoding("UTF-8");
			freemarkerConfig.setLocalizedLookup(false);
			freemarkerConfig.setTemplateUpdateDelayMilliseconds(6000);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}


	public static String generateVerificationLink(String url, String username) {
		HashMap<String, String> model = new HashMap<>();
		model.put("name", username);
		model.put("url", url);
		return createBody(EMAIL_TEMPLATE, model);
	}

	//    public String resetPassword(String url, String username, String passwordResetEmailTemplateName) {
	//        HashMap<String, String> model = new HashMap<>();
	//        model.put("username", username);
	//        model.put("url", url);
	//        model.put("name", username);
	//        model.put("systemName", "Whydah system" );
	//        if (passwordResetEmailTemplateName == null || passwordResetEmailTemplateName.length() < 4) {
	//            return createBody(EMAIL_TEMPLATE, model);
	//        }
	//        return createBody(passwordResetEmailTemplateName, model);
	//    }

	/*
    public String newUser(String name, String systemname, String url) {
        HashMap<String, String> model = new HashMap<>();
        model.put("name", name);
        model.put("url", url);
        return createBody(NEW_USER_EMAIL_TEMPLATE, model);
    }
	 */

	private static String createBody(String templateName, HashMap<String, String> model) {
		StringWriter stringWriter = new StringWriter();
		try {
			Template template = freemarkerConfig.getTemplate(templateName);
			template.process(model, stringWriter);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Populating template failed. templateName=" + templateName, e);
		}
		return stringWriter.toString();
	}
}
