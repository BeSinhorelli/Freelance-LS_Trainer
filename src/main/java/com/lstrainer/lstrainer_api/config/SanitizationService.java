package com.lstrainer.lstrainer_api.config;

import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class SanitizationService {
    public String clean(String input) {
        if (input == null) return null;
        return HtmlUtils.htmlEscape(input); 
    }
}