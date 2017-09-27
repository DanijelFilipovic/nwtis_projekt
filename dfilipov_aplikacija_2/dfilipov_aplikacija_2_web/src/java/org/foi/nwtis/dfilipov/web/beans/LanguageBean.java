package org.foi.nwtis.dfilipov.web.beans;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.faces.context.FacesContext;

@Named(value = "languageBean")
@SessionScoped
public class LanguageBean implements Serializable
{
	private static final Map<String, String> languages;
	private String currentLanguage;
	
	static {
		languages = new LinkedHashMap();
		languages.put("Hrvatski", "hr");
		languages.put("English", "en");
	}
	
	public LanguageBean()
	{
		currentLanguage = FacesContext.getCurrentInstance().getApplication().getDefaultLocale().getLanguage();
	}
	
	public Map<String, String> getLanguages()
	{
		return languages;
	}

	public String getCurrentLanguage()
	{
		return currentLanguage;
	}

	public void setCurrentLanguage(String currentLanguage)
	{
		this.currentLanguage = currentLanguage;
	}
}
