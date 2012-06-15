package com.atex;

import hudson.util.FormValidation;

import java.net.MalformedURLException;
import java.net.URL;

public class ValidationUtil {

	public static FormValidation checkUrl(String value) {
		if (value.length() == 0) {
			return FormValidation.error("Please set a URL");
		}
		try {
			new URL(value);
			return FormValidation.ok();
		} catch (MalformedURLException e) {
			return FormValidation.error("Malformed URL, " + e.getMessage());
		}
	}

	public static FormValidation checkIntValue(String value) {
		if (value != null && value.length() > 0) {
			try {
				Integer.parseInt(value);
			} catch (NumberFormatException e) {
				return FormValidation
						.error("Please enter a valid integer value, "
								+ e.getMessage());
			}
		}
		return FormValidation.ok();
	}

	public static FormValidation checkAuthString(String value) {
		if (value.length() > 0) {

			if (value.contains(":")) {
				return FormValidation.ok();
			}
		}
		return FormValidation.error("Please enter a value on the format 'username:password'");
	}

}
